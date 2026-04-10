package server.service;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.Transaction;
import org.springframework.stereotype.Service;
import server.model.Ticket;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class TicketService {

    private static final String COLLECTION       = "tickets";
    private static final String COUNTER_COLLECTION = "counters";
    private static final String COUNTER_DOC      = "tickets";
    private static final String COUNTER_FIELD    = "lastNumber";

    private final Firestore firestore;

    public TicketService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Creates a new ticket with a sequential ticket number (V000001, V000002, ...).
     * Uses a Firestore transaction on counters/tickets to safely increment the counter.
     */
    public Ticket createTicket(Ticket ticket) throws ExecutionException, InterruptedException {
        String ticketId = UUID.randomUUID().toString();
        ticket.setTicketId(ticketId);
        ticket.setStatus("PENDING");
        ticket.setArchived(false);
        ticket.setCreatedAt(new Date());

        var counterRef = firestore.collection(COUNTER_COLLECTION).document(COUNTER_DOC);
        var ticketRef  = firestore.collection(COLLECTION).document(ticketId);

        firestore.runTransaction((Transaction.Function<Void>) transaction -> {
            var counterSnap = transaction.get(counterRef).get();
            long next = 1;
            if (counterSnap.exists() && counterSnap.contains(COUNTER_FIELD)) {
                next = counterSnap.getLong(COUNTER_FIELD) + 1;
            }

            ticket.setTicketNumber(String.format("V%06d", next));

            Map<String, Object> counterUpdate = new HashMap<>();
            counterUpdate.put(COUNTER_FIELD, next);
            transaction.set(counterRef, counterUpdate);
            transaction.set(ticketRef, ticket);
            return null;
        }).get();

        return ticket;
    }

    /**
     * Updates the status of a ticket.
     * Pass staffId when a staff member claims a PENDING ticket (IN_PROGRESS).
     * Pass null for staffId on COMPLETED or CANCELLED transitions.
     */
    public void updateStatus(String ticketId, String status, String staffId)
            throws ExecutionException, InterruptedException {

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);

        if (staffId != null) {
            updates.put("assignedStaffId", staffId);
        }

        if ("COMPLETED".equals(status) || "CANCELLED".equals(status)) {
            updates.put("archived", true);
            updates.put("completedAt", new Date());
        }

        firestore.collection(COLLECTION)
                .document(ticketId)
                .update(updates)
                .get();
    }

    /**
     * Returns all tickets for a customer, newest first.
     */
    public List<Ticket> getTicketsByCustomer(String customerId)
            throws ExecutionException, InterruptedException {

        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION)
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get()
                .getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(Ticket.class))
                .toList();
    }

    /**
     * Returns all PENDING, non-archived tickets ordered oldest-first.
     * This is the staff queue — first come, first served.
     */
    public List<Ticket> getPendingQueue() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> docs = firestore.collection(COLLECTION)
                .whereEqualTo("status", "PENDING")
                .whereEqualTo("archived", false)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .get()
                .getDocuments();

        return docs.stream()
                .map(doc -> doc.toObject(Ticket.class))
                .toList();
    }
}
