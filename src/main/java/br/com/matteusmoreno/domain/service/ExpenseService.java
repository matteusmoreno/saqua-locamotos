package br.com.matteusmoreno.domain.service;

import br.com.matteusmoreno.application.utils.DateUtils;
import br.com.matteusmoreno.domain.constant.PaymentStatus;
import br.com.matteusmoreno.domain.dto.request.CreateExpenseRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterExpenseRequestDto;
import br.com.matteusmoreno.domain.entity.Expense;
import br.com.matteusmoreno.domain.entity.Motorcycle;
import br.com.matteusmoreno.domain.repository.ExpenseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ApplicationScoped
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final FinancialService financialService;
    private final MotorcycleService motorcycleService;
    private final DateUtils dateUtils;

    public ExpenseService(ExpenseRepository expenseRepository, FinancialService financialService, MotorcycleService motorcycleService, DateUtils dateUtils) {
        this.expenseRepository = expenseRepository;
        this.financialService = financialService;
        this.motorcycleService = motorcycleService;
        this.dateUtils = dateUtils;
    }

    public Expense createExpense(CreateExpenseRequestDto request) {
        log.info("Creating expense with data: {}", request);
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(request.motorcycleId());
        LocalDateTime now = this.dateUtils.now();

        Expense expense = Expense.builder()
                .motorcycleId(request.motorcycleId())
                .type(request.type())
                .amount(request.amount())
                .description(request.description())
                .dueDate(request.dueDate())
                .status(PaymentStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.expenseRepository.persist(expense);
        this.financialService.saveExpense(motorcycle, expense);

        log.info("Expense created with ID: {} for motorcycle: {}", expense.getExpenseId(), request.motorcycleId());
        return expense;
    }

    public Expense findExpenseById(String id) {
        log.info("Finding expense with ID: {}", id);
        Expense expense = this.expenseRepository.findExpenseById(id);
        log.info("Expense found: {}", expense);
        return expense;
    }

    public Expense registerExpensePayment(RegisterExpenseRequestDto request) {
        log.info("Registering payment for expense with ID: {}", request.expenseId());
        Expense expense = this.findExpenseById(request.expenseId());
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(expense.getMotorcycleId());
        LocalDate today = this.dateUtils.today();

        expense.setPaidDate(today);
        expense.setStatus(PaymentStatus.PAID);
        expense.setMethod(request.method());
        expense.setUpdatedAt(this.dateUtils.now());

        this.expenseRepository.update(expense);

        // Sincroniza o Financial da moto com a expense atualizada (status PAID)
        this.financialService.updateExpense(motorcycle, expense);

        log.info("Expense payment registered: {}", expense);
        return expense;
    }

    public Expense deleteExpense(String expenseId) {
        log.info("Deleting expense with ID: {}", expenseId);
        Expense expense = this.findExpenseById(expenseId);
        Motorcycle motorcycle = this.motorcycleService.findMotorcycleById(expense.getMotorcycleId());

        this.expenseRepository.delete(expense);
        this.financialService.removeExpense(motorcycle, expenseId);

        log.info("Expense deleted: {}", expense);
        return expense;
    }
}
