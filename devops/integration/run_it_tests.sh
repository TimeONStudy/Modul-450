#!/usr/bin/env bash
# Integration smoke + Schwerpunkt-Tests für M450_lib
# Voraussetzung: Server läuft mit --spring.profiles.active=test

set -u

BASE="${BASE:-http://localhost:8080}"
JQ="$(command -v jq || true)"

line() { printf '%*s\n' "${COLUMNS:-80}" '' | tr ' ' '-'; }
show() { echo; line; echo "# $1"; line; }

dump() {
  local body="$1"
  if [ -n "$JQ" ]; then
    echo "$body" | jq . 2>/dev/null || echo "$body"
  else
    echo "$body"
  fi
}

get() {
  local path="$1"
  show "GET  $path"
  local res
  res="$(curl -sS -w $'\n%{http_code}' "$BASE$path")" || { echo "curl failed"; return 1; }
  local code="${res##*$'\n'}"
  local body="${res%$'\n'*}"
  echo "HTTP $code"
  dump "$body"
}

post_json() {
  local path="$1" json="$2"
  show "POST $path"
  echo "Payload: $json"
  local res
  res="$(curl -sS -w $'\n%{http_code}' -H 'Content-Type: application/json' -d "$json" "$BASE$path")" || { echo "curl failed"; return 1; }
  local code="${res##*$'\n'}"
  local body="${res%$'\n'*}"
  echo "HTTP $code"
  dump "$body"
}

echo "Base URL: $BASE"

# --- 1) DB-Verbindung ---
get /api/book/getBooks

# --- 2) Seed-Konsistenz ---
get /api/book/getBookById/b2

# --- 3) Transaktion: Rent/Return ---
# Rent b1 durch u1 (Happy Path)
post_json /api/book/rentBook  '{"bookId":"b1","userId":"u1"}'
get /api/book/getBookById/b1

# Re-rent b2 durch u2 (Fehler erwartet: 400/409, Zustand unverändert)
post_json /api/book/rentBook  '{"bookId":"b2","userId":"u2"}'
get /api/book/getBookById/b2

# Return b2 durch u2 (Fehler erwartet: 400/401/403)
post_json /api/book/returnBook '{"bookId":"b2","userId":"u2"}'

# Return b2 durch u1 (Happy Path) + Zustand prüfen
post_json /api/book/returnBook '{"bookId":"b2","userId":"u1"}'
get /api/book/getBookById/b2

# --- 4) Fehlerbehandlung ---
# Not Found
get /api/book/getBookById/does-not-exist

# Validation: fehlendes userId beim Rent
post_json /api/book/rentBook '{"bookId":"b1"}'

# --- 5) Kategorien & Users (Happy Paths) ---
get /api/category/getCategories
get /api/category/getBooksByCategory/cat-1
get /api/category/getBooksByCategory/cat-2

get /api/user/getUsers
get /api/user/getUserById/u1
get /api/user/getUserRentedBooks/u1

echo
line
echo "# Fertig."
line
