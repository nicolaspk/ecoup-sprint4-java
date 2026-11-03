package br.com.fiap.resource;

import br.com.fiap.bo.AcaoBO;
import br.com.fiap.to.AcaoTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/acoes")
public class AcaoResource {
    private AcaoBO acaoBO = new AcaoBO();

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<AcaoTO> acoes = acaoBO.findAll();
            return ResponseEntity.ok(acoes);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao consultar acoes");
        }
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> findByCodigo(@PathVariable Long codigo) {
        try {
            AcaoTO acao = acaoBO.findByCodigo(codigo);
            if (acao != null) return ResponseEntity.ok(acao);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acao nao encontrada");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao consultar acao");
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid AcaoTO acao) {
        try {
            AcaoTO salva = acaoBO.save(acao);
            if (salva != null) return ResponseEntity.status(HttpStatus.CREATED).body(salva);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar acao");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar acao");
        }
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> update(@PathVariable Long codigo, @RequestBody @Valid AcaoTO acao) {
        try {
            acao.setCodigo(codigo);
            AcaoTO atualizada = acaoBO.update(acao);
            if (atualizada != null) return ResponseEntity.ok(atualizada);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acao nao encontrada");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar acao");
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> delete(@PathVariable Long codigo) {
        try {
            if (acaoBO.delete(codigo)) return ResponseEntity.noContent().build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acao nao encontrada");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir acao");
        }
    }
}
