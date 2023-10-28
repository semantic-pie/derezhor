package io.github.semanticpie.derezhor.externalAgents.exampleAgent;

import io.github.semanticpie.derezhor.externalAgents.exampleAgent.models.SimpleDTO;
import io.github.semanticpie.derezhor.externalAgents.exampleAgent.services.ExampleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping(path = "/")
@AllArgsConstructor
public class ExampleAgentController {

    private ExampleService exampleService;

    @GetMapping()
    ResponseEntity<SimpleDTO> response(@RequestParam String main_idtf, @RequestParam int jmantic) {
        try {
            SimpleDTO simpleDTO = null;
            switch (jmantic){
                case 0: {
                    simpleDTO = SimpleDTO.builder().addr(exampleService.getByName(main_idtf).getAddress()).build();
                }
                case 1: {
                    simpleDTO = SimpleDTO.builder().addr(exampleService.getByNameWithJmanticService(main_idtf).getAddress()).build();
                }
            }
            return ResponseEntity.ok(simpleDTO);
        } catch (Exception ex) {
            log.error(Arrays.toString(ex.getStackTrace()));
            return ResponseEntity.status(500).build();
        }
    }

}