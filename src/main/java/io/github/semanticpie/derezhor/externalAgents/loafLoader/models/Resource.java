package io.github.semanticpie.derezhor.externalAgents.loafLoader.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    private InputStream fileStream;

    private MediaType type;

}
