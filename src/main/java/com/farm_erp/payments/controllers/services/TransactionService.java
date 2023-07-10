package com.farm_erp.payments.controllers.services;

import com.farm_erp.accounting.domains.FarmerMoneyActivity;
import com.farm_erp.accounting.domains.TransporterMoneyActivity;
import com.farm_erp.accounting.statics.ActivityType;
import com.farm_erp.auth.domain.User;
import com.farm_erp.outgrowers.domains.Farmer;
import com.farm_erp.payments.controllers.services.payloads.TransactionRequest;
import com.farm_erp.payments.domains.Transaction;
import com.farm_erp.payments.statics.TransactionType;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import com.farm_erp.weigh_bridge.domains.Transporter;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class TransactionService {

    public Transaction create(TransactionRequest request, User user) {
        Farmer farmer = null;
        if(request.farmerId!=null) {
            farmer = Farmer.findById(request.farmerId);
            if (farmer == null) throw new WebApplicationException("Invalid farmer selected!", 404);
        }

        Transporter transporter = null;
        if(request.transporterId != null){
            transporter = Transporter.findById(request.transporterId);
            if(transporter == null) throw new WebApplicationException("Invalid transporter selected!", 404);
        }

        Transaction transaction = new Transaction(request.amount, request.type, farmer, transporter);
        transaction.persist();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.SAVED.toString(), _Section_Enums.TRANSACTIONS.toString(), transaction.id,
                null, jsonb.toJson(transaction), user);
        trail.persist();

        return transaction;
    }

    public Transaction update(Long id, TransactionRequest request, User user) {
        Transaction transaction = Transaction.findById(id);
        if (transaction == null) throw new WebApplicationException("Invalid transaction selected!", 404);

        Farmer farmer = null;
        if(request.farmerId!=null) {
            farmer = Farmer.findById(request.farmerId);
            if (farmer == null) throw new WebApplicationException("Invalid farmer selected!", 404);
        }

        Transporter transporter = null;
        if(request.transporterId != null){
            transporter = Transporter.findById(request.transporterId);
            if(transporter == null) throw new WebApplicationException("Invalid transporter selected!", 404);
        }

        Transaction oldTransaction = transaction;
        transaction.amount = request.amount;
        transaction.farmer = farmer;
        transaction.transporter = transporter;
        transaction.transactionType = request.type.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.UPDATED.toString(), _Section_Enums.TRANSACTIONS.toString(), transaction.id,
                jsonb.toJson(oldTransaction), jsonb.toJson(transaction), user);
        trail.persist();

        return transaction;
    }

    public Transaction approve(Long id, User user) {
        Transaction transaction = Transaction.findById(id);
        if (transaction == null) throw new WebApplicationException("Invalid transaction selected", 404);

        Transaction oldTransaction = transaction;
        transaction.status = _StatusTypes_Enum.APPROVED.toString();

        if(transaction.farmer != null) {
            if (transaction.transactionType.equals(TransactionType.WITHDRAW.toString())) {
                FarmerMoneyActivity far1 = new FarmerMoneyActivity(
                        transaction.amount.negate(),
                        "Payment",
                        ActivityType.WITHDRAW.toString(), transaction.id,
                        transaction.farmer
                );
                far1.persist();
            } else {
                FarmerMoneyActivity far1 = new FarmerMoneyActivity(
                        transaction.amount,
                        "Teller Deposit",
                        ActivityType.DEPOSIT.toString(), transaction.id,
                        transaction.farmer
                );
                far1.persist();
            }
        }

        if(transaction.transporter != null) {
            if (transaction.transactionType.equals(TransactionType.WITHDRAW.toString())) {
                TransporterMoneyActivity far1 = new TransporterMoneyActivity(
                        transaction.amount.negate(),
                        "Payment",
                        ActivityType.WITHDRAW.toString(), transaction.id,
                        transaction.transporter
                );
                far1.persist();
            } else {
                TransporterMoneyActivity far1 = new TransporterMoneyActivity(
                        transaction.amount,
                        "Teller Deposit",
                        ActivityType.DEPOSIT.toString(), transaction.id,
                        transaction.transporter
                );
                far1.persist();
            }
        }

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.APPROVED.toString(), _Section_Enums.TRANSACTIONS.toString(), transaction.id,
                jsonb.toJson(oldTransaction), jsonb.toJson(transaction), user);
        trail.persist();

        return transaction;
    }

    public Transaction reject(Long id, User user) {
        Transaction transaction = Transaction.findById(id);
        if (transaction == null) throw new WebApplicationException("Invalid transaction selected", 404);

        Transaction oldTransaction = transaction;
        transaction.status = _StatusTypes_Enum.REJECTED.toString();

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.REJECTED.toString(), _Section_Enums.TRANSACTIONS.toString(), transaction.id,
                jsonb.toJson(oldTransaction), jsonb.toJson(transaction), user);
        trail.persist();

        return transaction;
    }

    public List<Transaction> get(LocalDate start, LocalDate end, String status) {
        List<String> statuses = new ArrayList<>();
        statuses.add(_StatusTypes_Enum.PENDING.toString());
        statuses.add(_StatusTypes_Enum.APPROVED.toString());
        statuses.add(_StatusTypes_Enum.DELETED.toString());

        if (status != null && !statuses.contains(status))
            throw new WebApplicationException("Invalid status selected", 404);

        if (start == null) start = LocalDate.now().withDayOfMonth(01);
        if (end == null) end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        return Transaction.list("(?1 is null or status=?1) and DATE(entryDate) between ?2 and ?3", status, Date.valueOf(start), Date.valueOf(end));
    }

    public Transaction delete(Long id, User user) {
        Transaction transaction = Transaction.findById(id);
        if (transaction == null) throw new WebApplicationException("Invalid transaction selected", 404);

        if (transaction.status.equals(_StatusTypes_Enum.APPROVED.toString()))
            throw new WebApplicationException("Approved transactions cannot be deleted!", 406);

        Transaction oldTransaction = transaction;

        transaction.status = _StatusTypes_Enum.DELETED.toString();
        transaction.deleted = 1;

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.DELETED.toString(), _Section_Enums.TRANSACTIONS.toString(), transaction.id,
                jsonb.toJson(oldTransaction), jsonb.toJson(transaction), user);
        trail.persist();

        return transaction;
    }
}
