package br.com.matteusmoreno.domain.controller;

import br.com.matteusmoreno.domain.dto.request.CreateExpenseRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterExpenseRequestDto;
import br.com.matteusmoreno.domain.entity.Expense;
import br.com.matteusmoreno.domain.service.ExpenseService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public Expense createExpense(CreateExpenseRequestDto request) {
        return this.expenseService.createExpense(request);
    }

    public Expense findExpenseById(String expenseId) {
        return this.expenseService.findExpenseById(expenseId);
    }

    public Expense registerExpense(RegisterExpenseRequestDto request) {
        return this.expenseService.registerExpensePayment(request);
    }

    public Expense deleteExpense(String expenseId) {
        return this.expenseService.deleteExpense(expenseId);
    }
}
