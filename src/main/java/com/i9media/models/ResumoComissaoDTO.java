package com.i9media.models;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResumoComissaoDTO {

    private String executivo;
    private int totalPis;
    private BigDecimal totalComissao;

    public ResumoComissaoDTO(String executivo, int totalPis, BigDecimal totalComissao) {
        this.executivo = executivo;
        this.totalPis = totalPis;
        this.totalComissao = totalComissao != null ? totalComissao : BigDecimal.ZERO;
    }

    public String getExecutivo() {
        return executivo;
    }

    public void setExecutivo(String executivo) {
        this.executivo = executivo;
    }

    public int getTotalPis() {
        return totalPis;
    }

    public void setTotalPis(int totalPis) {
        this.totalPis = totalPis;
    }

    public BigDecimal getTotalComissao() {
        return totalComissao;
    }

    public void setTotalComissao(BigDecimal totalComissao) {
        this.totalComissao = totalComissao;
    }

    public static List<ResumoComissaoDTO> gerarResumo(List<ComissaoDTO> comissoes) {
        Map<String, List<ComissaoDTO>> agrupadoPorExecutivo = comissoes.stream()
            .collect(Collectors.groupingBy(ComissaoDTO::getExecutivo));

        return agrupadoPorExecutivo.entrySet().stream()
            .map(entry -> {
                String executivo = entry.getKey();
                List<ComissaoDTO> lista = entry.getValue();

                int totalPis = lista.size();
                BigDecimal totalComissao = lista.stream()
                    .map(ComissaoDTO::getComissaoCalculada)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                return new ResumoComissaoDTO(executivo, totalPis, totalComissao);
            })
            .collect(Collectors.toList());
    }
}