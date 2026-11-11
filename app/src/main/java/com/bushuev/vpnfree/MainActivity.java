package com.bushuev.vpnfree;
import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private static final int REQ_VPN = 1001;
    private EditText etHost, etPort;
    private Button btnToggle;
    private TextView tvStatus;
    private boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        etHost = new EditText(this); etHost.setHint("Host"); etHost.setText("127.0.0.1");
        etPort = new EditText(this); etPort.setHint("Port"); etPort.setText("1080");
        btnToggle = new Button(this); btnToggle.setText("Подключить");
        tvStatus = new TextView(this); tvStatus.setText("Статус: отключено");
        layout.addView(etHost); layout.addView(etPort); layout.addView(btnToggle); layout.addView(tvStatus);
        setContentView(layout);

        btnToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (connected) {
                    Intent i = new Intent(MainActivity.this, BushuevVpnService.class);
                    i.setAction(BushuevVpnService.ACTION_DISCONNECT);
                    startService(i); setConnected(false); return;
                }
                Intent prepare = VpnService.prepare(MainActivity.this);
                if (prepare != null) startActivityForResult(prepare, REQ_VPN);
                else onActivityResult(REQ_VPN, RESULT_OK, null);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQ_VPN) return;
        if (resultCode != RESULT_OK) return;
        Intent i = new Intent(this, BushuevVpnService.class);
        i.setAction(BushuevVpnService.ACTION_CONNECT);
        i.putExtra("host", etHost.getText().toString());
        i.putExtra("port", Integer.parseInt(etPort.getText().toString()));
        startService(i); setConnected(true);
    }

    private void setConnected(boolean value) {
        connected = value;
        btnToggle.setText(value ? "Отключить" : "Подключить");
        tvStatus.setText(value ? "Статус: подключено" : "Статус: отключено");
        etHost.setEnabled(!value); etPort.setEnabled(!value);
    }
}
