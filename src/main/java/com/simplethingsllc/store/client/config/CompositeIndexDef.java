package com.simplethingsllc.store.client.config;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class CompositeIndexDef {
  @NotNull @NotEmpty public String kind;
  @Size(min = 2) public Set<String> props = new HashSet<>();
}
