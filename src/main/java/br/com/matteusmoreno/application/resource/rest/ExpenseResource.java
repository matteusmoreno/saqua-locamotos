package br.com.matteusmoreno.application.resource.rest;

import br.com.matteusmoreno.application.common.RequestParam;
import br.com.matteusmoreno.domain.controller.ExpenseController;
import br.com.matteusmoreno.domain.dto.request.CreateExpenseRequestDto;
import br.com.matteusmoreno.domain.dto.request.RegisterExpenseRequestDto;
import br.com.matteusmoreno.domain.dto.response.ExpenseResponseDto;
import br.com.matteusmoreno.domain.entity.Expense;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/expenses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExpenseResource {

    private final ExpenseController expenseController;

    public ExpenseResource(ExpenseController expenseController) {
        this.expenseController = expenseController;
    }

    @POST
    @Path("/create")
    @RolesAllowed("ADMIN")
    public Response create(@Valid CreateExpenseRequestDto request) {
        Expense expense = this.expenseController.createExpense(request);
        return Response.status(Response.Status.CREATED).entity(new ExpenseResponseDto(expense)).build();
    }

    @GET
    @Path("/{expenseId}")
    @RolesAllowed({"ADMIN", "USER"})
    public Response findById(@PathParam(RequestParam.EXPENSE_ID) String expenseId) {
        Expense expense = this.expenseController.findExpenseById(expenseId);
        return Response.status(Response.Status.OK).entity(new ExpenseResponseDto(expense)).build();
    }

    @PATCH
    @Path("/register")
    @RolesAllowed("ADMIN")
    public Response register(@Valid RegisterExpenseRequestDto request) {
        Expense expense = this.expenseController.registerExpense(request);
        return Response.status(Response.Status.OK).entity(new ExpenseResponseDto(expense)).build();
    }

    @DELETE
    @Path("/{expenseId}")
    @RolesAllowed("ADMIN")
    public Response delete(@PathParam(RequestParam.EXPENSE_ID) String expenseId) {
        this.expenseController.deleteExpense(expenseId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
