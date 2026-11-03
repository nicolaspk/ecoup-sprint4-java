package br.com.fiap.to;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AcaoTO {
    private Long codigo;

    @NotNull
    @Positive
    private Long usuarioCodigo;

    @NotBlank
    @Size(max = 200)
    private String titulo;

    @NotBlank
    private String categoria;

    @NotBlank
    @Size(max = 1000)
    private String linkEvidencia;

    private LocalDate dataRegistro;
    private String status;

    public AcaoTO() { }

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public Long getUsuarioCodigo() { return usuarioCodigo; }
    public void setUsuarioCodigo(Long usuarioCodigo) { this.usuarioCodigo = usuarioCodigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getLinkEvidencia() { return linkEvidencia; }
    public void setLinkEvidencia(String linkEvidencia) { this.linkEvidencia = linkEvidencia; }
    public LocalDate getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDate dataRegistro) { this.dataRegistro = dataRegistro; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
