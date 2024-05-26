
import org.example.constant.BillState;
import org.example.model.Bill;
import org.example.service.impl.PaymentServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Scanner;

import static org.junit.Assert.*;

public class PaymentServiceTest {
    private PaymentServiceImpl paymentService;
    private ByteArrayOutputStream outContent;


    @Before
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String input = ""; // Provide a default empty input for initialization
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inContent);
        paymentService = new PaymentServiceImpl(scanner);
    }

    @Test
    public void givenBill_createBill_returnBill() throws ParseException {

        Bill bill = new Bill(4, "TEST", 300000, "15/11/2020", BillState.NOT_PAID, "TEST Provider");
        paymentService.createBill(bill);
        assertEquals("Bill created successfully: 4. TEST 300000.0 15/11/2020 NOT_PAID TEST Provider\r\n", outContent.toString());
    }

    @Test
    public void givenBillId_updateBill_returnBillNotFound() throws ParseException {
        Bill updatedBill = new Bill(10, "WATER", 200000, "30/10/2020", BillState.NOT_PAID, "SAVACO HCMC");
        paymentService.updateBill(updatedBill);
        assertTrue(outContent.toString().contains("Bill not found!"));
    }

    @Test
    public void givenBillId_viewBill_returnBillNotFound() {
        paymentService.viewBill(10);
        assertTrue(outContent.toString().contains("Bill not found!"));
    }

    @Test
    public void givenBillId_deleteBill_returnBillNotFound() {
        paymentService.deleteBill(10);
        assertTrue(outContent.toString().contains("Bill not found!"));
    }

    @Test
    public void givenBillId_updateBill_returnResult() throws ParseException {
        Bill existingBill = new Bill(2, "WATER", 175000, "30/10/2020", BillState.NOT_PAID, "SAVACO HCMC");
        Bill updatedBill = new Bill(2, "WATER", 200000, "30/10/2020", BillState.NOT_PAID, "SAVACO HCMC");
        paymentService.updateBill(updatedBill);
        assertEquals("Bill updated successfully: 2. WATER 200000.0 30/10/2020 NOT_PAID SAVACO HCMC\r\n", outContent.toString());
    }

    @Test
    public void givenBillId_deleteBill_returnResult() {
        paymentService.deleteBill(2);
        assertEquals("Bill deleted successfully: 2. WATER 175000.0 30/10/2020 NOT_PAID SAVACO HCMC\r\n", outContent.toString());
    }

    @Test
    public void givenBillId_viewBill_returnResult() {
        paymentService.viewBill(1);
        assertTrue(outContent.toString().contains("1. ELECTRIC 200000.0 25/10/2020 NOT_PAID EVN HCMC"));
    }
    @Test
    public void givenCashIn_WhenAddCash_returnBalance() {
        paymentService.cashIn(1000000);
        assertEquals("Your available balance: 1000000.0\r\n", outContent.toString());
    }

    @Test
    public void getListBill_returnListBill() {
        paymentService.listBills();
        String output = outContent.toString();
        assertTrue(output.contains("1. ELECTRIC 200000.0 25/10/2020 NOT_PAID EVN HCMC"));
        assertTrue(output.contains("2. WATER 175000.0 30/10/2020 NOT_PAID SAVACO HCMC"));
        assertTrue(output.contains("3. INTERNET 800000.0 30/11/2020 NOT_PAID VNPT"));
    }

    @Test
    public void givenBillId_payBill_returnPaymentAndBalance() {
        paymentService.cashIn(1000000);
        paymentService.payBills(new String[]{"1"});
        String output = outContent.toString();
        assertTrue(output.contains("Payment has been completed for Bill with id 1."));
        assertTrue(output.contains("Your current balance is: 800000.0"));
    }

    @Test
    public void givenBillId_notEnoughFund_returnError() {
        paymentService.cashIn(100000);
        paymentService.payBills(new String[]{"1"});
        String output = outContent.toString();
        assertTrue(output.contains("Sorry! Not enough fund to proceed with payment."));
    }

    @Test
    public void givenBillId_notFound_returnError() {
        paymentService.cashIn(1000000);
        paymentService.payBills(new String[]{"10"});
        String output = outContent.toString();
        assertTrue(output.contains("Sorry! Not found a bill with such id: 10"));
    }

    @Test
    public void givenBillIdAndScheduleDate_schedulePayment_returnMessage() throws ParseException {
        paymentService.schedulePayment(2, "28/10/2020");
        String output = outContent.toString();
        assertTrue(output.contains("Payment for bill id 2 is scheduled on 28/10/2020"));
    }

    @Test
    public void givenBillIdAndScheduleDate_schedulePayment_returnError() {
        paymentService.cashIn(1000000);
        paymentService.payBills(new String[]{"1"});
        paymentService.schedulePayment(1, "28/10/2020");
        String output = outContent.toString();
        assertTrue(output.contains("Bill with id 1 has already been paid. Cannot schedule payment."));
    }

    @Test
    public void givenInvalidScheduleDate_schedulePayment_returnError() {
        paymentService.schedulePayment(2, "invalid-date");
        String output = outContent.toString();
        assertTrue(output.contains("Invalid date format!"));
    }

    @Test
    public void getListPayment_ReturnListPayment() throws ParseException {
        paymentService.schedulePayment(2, "28/10/2020");
        paymentService.listPayments();
        String output = outContent.toString();
        assertTrue(output.contains("1. 175000.0 28/10/2020 PENDING 2"));
    }

    @Test
    public void givenProvider_searchBill_returnBill() {
        paymentService.searchBillByProvider("VNPT");
        String output = outContent.toString();
        assertTrue(output.contains("3. INTERNET 800000.0 30/11/2020 NOT_PAID VNPT"));
    }

    @Test
    public void givenProvider_searchBill_returnNotFound() {
        paymentService.searchBillByProvider("NON_EXISTENT_PROVIDER");
        String output = outContent.toString();
        assertFalse(output.contains("NON_EXISTENT_PROVIDER"));
    }
}
