package com.ewu.moonx.App;

import com.ewu.moonx.UI.UserPkj.Name;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FirebaseOrderHandler {

    ArrayList localList;
    List<DocumentSnapshot> documents;
    OrderListener listener;

    public FirebaseOrderHandler(ArrayList localList, List<DocumentSnapshot> documents) {
        this.localList = localList;
        this.documents = documents;
    }

    public FirebaseOrderHandler setListener(OrderListener listener) {
        this.listener = listener;
        return this;
    }


    public interface OrderListener {
        int onPreDocsOrder(DocumentSnapshot o1, DocumentSnapshot o2);

        void onAdd(int firebaseIndex, int localIndex);

        void onRemove(int localIndex);

        int onCompare(int firebaseIndex, int localIndex);
    }


    public void startOrder() {
        Collections.sort(documents, new Comparator<DocumentSnapshot>() {
            @Override
            public int compare(DocumentSnapshot o1, DocumentSnapshot o2) {
                return listener.onPreDocsOrder(o1,o2);
            }
        });

        if (localList.size() == 0) {
            for (int i = 0; i < documents.size(); i++) {
                listener.onAdd(i, localList.size());
            }
        } else {

            int i = 0;
            int j = 0;

            while (i < documents.size() && j < localList.size()) {
                int compare = listener.onCompare(i, j);

                if (compare < 0) {
                    listener.onAdd(i, j);
                    i++;
                    j++;
                } else if (compare == 0) {
                    i++;
                    j++;
                } else {
                    listener.onRemove(j);
                }
            }

            if (i < documents.size()) {
                for (int d = i; d < documents.size(); d++) {
                    listener.onAdd(d, localList.size());
                }
            } else if (j < localList.size()) {
                for (int d = j; d < localList.size(); d++) {
                    listener.onRemove(d);
                }
            }
        }
    }
}
