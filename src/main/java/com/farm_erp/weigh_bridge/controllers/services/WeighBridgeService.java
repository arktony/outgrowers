package com.farm_erp.weigh_bridge.controllers.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;

import com.farm_erp.accounting.domains.AidPaymentActivity;
import com.farm_erp.accounting.domains.FarmerMoneyActivity;
import com.farm_erp.accounting.domains.TransporterMoneyActivity;
import com.farm_erp.accounting.statics.ActivityType;
import com.farm_erp.outgrowers.domains.Permit;
import com.farm_erp.settings.domains.GeneralBusinessSettings;
import com.farm_erp.settings.statics._SettingParameter_Enums;
import com.farm_erp.statics._StatusTypes_Enum;
import com.farm_erp.trails.domains.AuditTrail;
import com.farm_erp.trails.statics._Action_Enums;
import com.farm_erp.trails.statics._Section_Enums;
import com.farm_erp.weigh_bridge.controllers.services.payloads.PermitData;
import com.farm_erp.weigh_bridge.controllers.services.payloads.SingleRequest;
import com.farm_erp.weigh_bridge.domains.DeliveryNote;
import com.farm_erp.weigh_bridge.domains.TransportFares;
import com.farm_erp.weigh_bridge.domains.WeighBridgeTicket;
import com.farm_erp.weigh_bridge.statics.WeighBridgeStatusEnums;

@ApplicationScoped
public class WeighBridgeService {

    public PermitData getDetails(String serialNumber) {
        Permit permit = Permit.findBySerialNumber(serialNumber);
        if (permit == null)
            throw new WebApplicationException("Invalid permit selected", 404);

        PermitData perm = new PermitData();
        perm.farmerId = permit.getFarmerData().id;
        perm.farmerCode = permit.getFarmerData().code;
        perm.farmerName = permit.getFarmerData().firstName + " " + permit.getFarmerData().lastName;
        perm.status = permit.status;
        perm.suppliedTonnes = permit.deliveredYield;
        perm.remainingTonnes = permit.getRemainingYield();

        return perm;
    }

    // public void captureWeight(String serialNumber, LoadedRequest request, String
    // accessKey) {
    // Permit permit = Permit.findBySerialNumber(serialNumber);
    // if (permit == null) throw new WebApplicationException("Invalid permit
    // selected!", 404);
    //
    // WeighBridgeTicket tick1 =
    // WeighBridgeTicket.getByIdentifier(request.identifier);
    // if (tick1 != null) throw new WebApplicationException("Identifier is already
    // attached to another permit!", 404);
    //
    // WeighBridgeTicket ticket = WeighBridgeTicket.getByPermitTare(permit);
    // if (ticket == null) {
    // ticket = new WeighBridgeTicket(permit);
    // ticket.uniqueIdentifier = request.identifier;
    // ticket.grossWeight = request.grossWeight;
    // ticket.loadTime = request.loadTime;
    // ticket.status = WeighBridgeStatusEnums.WEIGHED.toString();
    // ticket.persist();
    // } else {
    // ticket.grossWeight = request.grossWeight;
    // ticket.loadTime = request.loadTime;
    // ticket.status = WeighBridgeStatusEnums.WEIGHED.toString();
    // }
    //
    // Jsonb jsonb = JsonbBuilder.create();
    // AuditTrail trail = new AuditTrail(_Action_Enums.CAPTURE_WEIGHT.toString(),
    // _Section_Enums.WEIGHBRIDGE.toString(), ticket.id, null, jsonb.toJson(ticket),
    // accessKey);
    // trail.persist();
    //
    // }
    //
    // public void captureTareWeight(String serialNumber, UnLoadedRequest request,
    // String accessKey) {
    // Permit permit = Permit.findBySerialNumber(serialNumber);
    // if (permit == null) throw new WebApplicationException("Invalid permit
    // selected!", 404);
    //
    // WeighBridgeTicket tick = WeighBridgeTicket.getByPermitTare(permit);
    // if (tick == null) throw new WebApplicationException("Gross weight not
    // captured yet!", 406);
    //
    // WeighBridgeTicket tick1 =
    // WeighBridgeTicket.getByIdentifier(request.identifier);
    // if (tick1 == null) throw new WebApplicationException("Invalid identifier
    // sent!", 404);
    //
    // if (!Objects.equals(tick1.id, tick.id))
    // throw new WebApplicationException("The identifier isn't attached to the
    // permit serial number sent", 404);
    //
    // WeighBridgeTicket old = tick;
    //
    // GeneralBusinessSettings gen = GeneralBusinessSettings
    // .single(_SettingParameter_Enums.COMPULSORY_DEDUCTION.toString());
    //
    // tick.tareWeight = request.tareWeight;
    // tick.unloadTime = request.unloadTime;
    // tick.isDone = Boolean.TRUE;
    //
    // tick.compulsoryAppliedDeduction = Double.parseDouble(gen.settingValue);
    //
    // tick.permit.deliveredYield += tick.getNetWeight();
    //
    // if (tick.permit.deliveredYield > tick.permit.estimatedYield) {
    // tick.permit.status = _StatusTypes_Enum.ENDED.toString();
    // tick.permit.block.getActiveRatoon().status =
    // _StatusTypes_Enum.CLOSED.toString();
    //
    // if (tick.permit.block.getActiveRatoon().cropType.position >= 3) {
    // tick.permit.block.getActiveRatoon().status =
    // _StatusTypes_Enum.CLOSED.toString();
    // }
    // }
    //
    // // transport payment
    // TransportFares fare = TransportFares.find(tick.permit.block.distance);
    //
    // BigDecimal payment = BigDecimal.ZERO;
    //
    // DeliveryNote note = new DeliveryNote(tick.getNetWeight(),
    // (tick.permit.estimatedYield - tick.permit.deliveredYield), fare.cost,
    // payment, tick);
    // note.persist();
    //
    // if (tick.vehicle != null) {
    // TransporterMoneyActivity act = new TransporterMoneyActivity(
    // fare.cost,
    // "Payment for cane delivered. Delivery note serial number : " +
    // note.deliveryNoteNumber,
    // tick.vehicle.transporter
    // );
    // act.persist();
    // }
    //
    // // payment to farmer
    // GeneralBusinessSettings set =
    // GeneralBusinessSettings.single(_SettingParameter_Enums.PAYMENT_PER_TONNE.toString());
    // if (set.settingValue == null || set.settingValue == "")
    // throw new WebApplicationException("First set the payment per tonne in
    // settings", 404);
    //
    // BigDecimal pay = new
    // BigDecimal(set.settingValue).multiply(BigDecimal.valueOf(tick.getNetWeight()));
    //
    // List<AidPaymentActivity> pays =
    // AidPaymentActivity.findByBlockCycle(tick.permit.block.getActiveRatoon());
    // BigDecimal totalPayment = BigDecimal.ZERO;
    // for (AidPaymentActivity act : pays) {
    // totalPayment = totalPayment.add(act.amount);
    // }
    //
    // BigDecimal pending =
    // tick.permit.block.getActiveRatoon().getTotalAid().subtract(totalPayment);
    //
    // if (Boolean.TRUE.equals(tick.permit.block.getActiveRatoon().isAided) &&
    // pending.compareTo(BigDecimal.ZERO) > 0) {
    //
    // AidPaymentActivity actoi = new AidPaymentActivity(
    // pending,
    // "Installment payment for aid",
    // tick.permit.block.getActiveRatoon()
    // );
    // actoi.persist();
    //
    // pay = pay.subtract(pending);
    // }
    //
    // FarmerMoneyActivity far = new FarmerMoneyActivity(
    // pay,
    // "Payment for cane delivered. Delivery note serial number : " +
    // note.deliveryNoteNumber,
    // ActivityType.CANE_PAYMENT.toString(), tick.id,
    // tick.permit.block.farmer
    // );
    // far.persist();
    //
    // note.payment = pay;
    // note.persist();
    //
    // Jsonb jsonb = JsonbBuilder.create();
    // AuditTrail trail = new AuditTrail(_Action_Enums.CAPTURE_WEIGHT.toString(),
    // _Section_Enums.WEIGHBRIDGE.toString(), tick.id, jsonb.toJson(old),
    // jsonb.toJson(tick), accessKey);
    // trail.persist();
    //
    // }

    public void capture(SingleRequest request, String accessKey) {
        Permit permit = Permit.findBySerialNumber(request.permitSerialNumber);
        if (permit == null)
            throw new WebApplicationException("Invalid permit selected!", 404);

        WeighBridgeTicket ticket = WeighBridgeTicket.getByPermitTare(permit);
        if (ticket == null) {
            WeighBridgeTicket tick1 = WeighBridgeTicket.getByIdentifier(request.identifier);
            if (tick1 != null)
                throw new WebApplicationException("Identifier is already attached to another permit!", 406);

            if (request.grossWeight == null)
                request.grossWeight = 0.0;

            ticket = new WeighBridgeTicket(permit);
            ticket.uniqueIdentifier = request.identifier;
            ticket.grossWeight = request.grossWeight / 1000;
            if (request.grossWeight != 0.0)
                ticket.loadTime = request.loadTime;
            if (request.tareWeight != null)
                ticket.tareWeight = request.tareWeight / 1000;
            ticket.vehicleNumber = request.vehicleNumber;
            ticket.persist();
        } else {
            if (Objects.equals(ticket.uniqueIdentifier, request.identifier)
                    && ticket.status.equals(WeighBridgeStatusEnums.WEIGHED.toString()))
                throw new WebApplicationException("The session for this permit and identifier is already closed", 406);

            if (!Objects.equals(ticket.uniqueIdentifier, request.identifier))
                throw new WebApplicationException("The identifier isn't attached to the permit serial number sent.",
                        406);

            if (request.tareWeight == null || request.tareWeight == 0)
                throw new WebApplicationException("Please provide a tare weight", 404);

            if (request.tareWeight.compareTo(request.grossWeight) > 0)
                throw new WebApplicationException("Tare weight cannot be greater than the gross weight", 406);

            if (request.additionalAppliedTonneDeduction == null)
                request.additionalAppliedTonneDeduction = 0.0;

            GeneralBusinessSettings gen = GeneralBusinessSettings
                    .single(_SettingParameter_Enums.COMPULSORY_DEDUCTION.toString());

            ticket.status = WeighBridgeStatusEnums.WEIGHED.toString();
            ticket.grossWeight = request.grossWeight / 1000;
            ticket.tareWeight = request.tareWeight / 1000;
            ticket.unloadTime = request.unloadTime;
            ticket.isDone = Boolean.TRUE;
            ticket.compulsoryAppliedDeduction = Double.parseDouble(gen.settingValue);
            ticket.additionalAppliedTonneDeduction = request.additionalAppliedTonneDeduction;
            ticket.permit.deliveredYield += ticket.getNetWeight();

            if (ticket.permit.deliveredYield > ticket.permit.estimatedYield) {
                ticket.permit.status = _StatusTypes_Enum.ENDED.toString();

                if (ticket.permit.block.getActiveRatoon().cropType.position >= 3) {
                    ticket.permit.block.getActiveRatoon().status = _StatusTypes_Enum.CLOSED.toString();
                }
            }

            GeneralBusinessSettings set = GeneralBusinessSettings
                    .single(_SettingParameter_Enums.PAYMENT_PER_TONNE.toString());
            if (set.settingValue == null || "".equals(set.settingValue))
                throw new WebApplicationException("First set the payment per tonne in settings", 404);

            BigDecimal pay = new BigDecimal(set.settingValue).multiply(BigDecimal.valueOf(ticket.getNetWeight()));

            // transport payment
            BigDecimal tp = BigDecimal.ZERO;
            TransportFares fare = TransportFares.find(ticket.permit.block.distance);
            if (fare != null)
                tp = fare.cost;

            DeliveryNote note = new DeliveryNote(ticket.getNetWeight(),
                    (ticket.permit.estimatedYield - ticket.permit.deliveredYield), tp, pay, ticket);
            note.persist();

            if (ticket.vehicle != null) {
                TransporterMoneyActivity act = new TransporterMoneyActivity(
                        tp,
                        "Payment for cane delivered. Delivery note serial number : " + note.deliveryNoteNumber,
                        ActivityType.TRANSPORT_PAYMENT.toString(), ticket.id,
                        ticket.vehicle.transporter);
                act.persist();
            }

            // payment to farmer
            FarmerMoneyActivity far = new FarmerMoneyActivity(
                    pay,
                    "Payment for cane delivered. Delivery note serial number : " + note.deliveryNoteNumber,
                    ActivityType.CANE_PAYMENT.toString(), ticket.id,
                    ticket.permit.block.farmer);
            far.persist();

            List<AidPaymentActivity> pays = AidPaymentActivity.findByBlockCycle(ticket.permit.block.getActiveRatoon());
            BigDecimal totalPayment = BigDecimal.ZERO;
            for (AidPaymentActivity act : pays) {
                totalPayment = totalPayment.add(act.amount);
            }

            BigDecimal pending = ticket.permit.block.getActiveRatoon().getTotalAid().subtract(totalPayment);

            if (Boolean.TRUE.equals(ticket.permit.block.getActiveRatoon().isAided)
                    && pending.compareTo(BigDecimal.ZERO) > 0) {

                AidPaymentActivity actoi = new AidPaymentActivity(
                        pending,
                        "Installment payment for aid",
                        ticket.permit.block.getActiveRatoon());
                actoi.persist();

                FarmerMoneyActivity far1 = new FarmerMoneyActivity(
                        pending.negate(),
                        "Payment for aid received for the cycle " + ticket.permit.block.getActiveRatoon().cropType.name,
                        ActivityType.LOAN_REPAYMENT.toString(), actoi.id,
                        ticket.permit.block.farmer);
                far1.persist();
            }

        }

        Jsonb jsonb = JsonbBuilder.create();
        AuditTrail trail = new AuditTrail(_Action_Enums.CAPTURE_WEIGHT.toString(),
                _Section_Enums.WEIGHBRIDGE.toString(), ticket.id, null, jsonb.toJson(ticket), accessKey);
        trail.persist();

    }

}
