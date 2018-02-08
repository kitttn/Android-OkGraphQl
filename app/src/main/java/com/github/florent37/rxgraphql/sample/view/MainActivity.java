package com.github.florent37.rxgraphql.sample.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.florent37.okgraphql.OkGraphql;
import com.github.florent37.okgraphql.converter.GsonConverter;
import com.github.florent37.rxgraphql.sample.R;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.github.florent37.okgraphql.Field.newField;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.text1)
    TextView text1;
    @BindView(R.id.text2)
    TextView text2;
    @BindView(R.id.text3)
    TextView text3;

    private OkGraphql okGraphql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.okGraphql = new OkGraphql.Builder()
                .okClient(((MainApplication) getApplication()).getOkHttpClient())
                .baseUrl("https://stage.urbest.io/graphql")
                .converter(new GsonConverter(new Gson()))
                .build();

        queryApiVersion();
    }

    private void queryApiVersion() {
        okGraphql
                .query(" { api { version }}")
                .enqueue(responseString -> {
                    text1.setText(responseString);
                }, error -> {
                    error.printStackTrace();
                    text1.setText(error.getLocalizedMessage());
                });
    }

    private void query_hero_built() {
        okGraphql

                .query(newField()
                        .field(newField("hero")
                                .field("name")
                                .field(newField("friend")
                                        .field("name")
                                )
                        )
                )

                .toSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dataString -> text1.setText(dataString),
                        throwable -> text1.setText(throwable.getLocalizedMessage())
                );
    }
}
