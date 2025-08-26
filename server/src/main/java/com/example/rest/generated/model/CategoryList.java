package com.example.rest.generated.model;

import java.net.URI;
import java.util.Objects;
import com.example.rest.generated.model.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CategoryList
 */
@lombok.Data @lombok.AllArgsConstructor @lombok.Builder

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-09-10T14:14:03.624390+02:00[Europe/Zurich]", comments = "Generator version: 7.7.0")
public class CategoryList {

  @Valid
  private List<@Valid Category> categories = new ArrayList<>();

  public CategoryList() {
    super();
  }

}

