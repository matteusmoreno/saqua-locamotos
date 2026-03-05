package br.com.matteusmoreno.domain.model;

import br.com.matteusmoreno.domain.entity.Expense;
import br.com.matteusmoreno.domain.entity.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
public class Financial {

    @Builder.Default
    private List<Payment> earnings = new ArrayList<>();
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();
    private BigDecimal total;
}
