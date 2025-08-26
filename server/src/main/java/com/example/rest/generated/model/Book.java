package com.example.rest.generated.model;

import java.net.URI;
import java.util.Objects;
import com.example.rest.generated.model.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.NoSuchElementException;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Book
 */
@lombok.Data @lombok.AllArgsConstructor @lombok.Builder

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-09-10T14:14:03.624390+02:00[Europe/Zurich]", comments = "Generator version: 7.7.0")
public class Book {

  private String id;

  private String name;

  private String author;

  private Category category;

  private Boolean available;

  private JsonNullable<String> rentedBy = JsonNullable.<String>undefined();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private JsonNullable<OffsetDateTime> rentedDate = JsonNullable.<OffsetDateTime>undefined();

  public Book() {
    super();
  }

}

