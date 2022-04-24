package com.ismt.journeyjournal.journal;

public interface JournalListeners {
    void onJournalClicked(JournalEntities journalEntities, int position);

    void onDelete(JournalEntities entities);
}
