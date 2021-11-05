package com.example.locationtask6;

import androidx.annotation.NonNull;

import com.example.locationtask6.model.LoadData;
import com.example.locationtask6.view.App;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.*;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({FirebaseFirestore.class,DocumentReference.class})
public class TestClass {
    private DocumentReference mockedDocumentReference;
    private FirebaseFirestore mockedFirebaseDatabase;

    @Before
    public void before(){
       mockedDocumentReference = Mockito.mock(DocumentReference.class);
       mockedFirebaseDatabase = Mockito.mock(FirebaseFirestore.class);
    }

    @Test
    public void firstTest(){
        String user = "yWgHGzrB04gkFVYi6phVRLBKRJ23";
        String date = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        mockedDocumentReference = mockedFirebaseDatabase.collection(user+date).document("161846");
        mockedDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println(document.getString("time"));
                        assertEquals(document.getString("time"), "161846");
                    }
                }
            }
        });
    }
}
