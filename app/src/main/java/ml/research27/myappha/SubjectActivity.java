package ml.research27.myappha;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class SubjectActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

//        final String[] url =  new String[] {
//                "dvd/M456/CHEMISTRY/p-CHEMISTRY.png",
//                "",
//                ""
//        };
        final String[] str = new String[] {
                "Chemistry2",
                "Math2",
                "Physics2",
                "English1",
                "Math1",
                "Science1",
                "Thai1",
                "English2",
                "Intenive2",
                "SOC"};

        final ListView listView = (ListView) findViewById(R.id.subject);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.layout, R.id.layout, str));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(getApplicationContext(), CourseActivity.class);
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), ""+str[position], Toast.LENGTH_LONG).show();
                switch (position) {
                    case 0:
                        intent.putExtra("subject", new String[]{
                                //"ติวเคมี กรดเบส",
                                "tuchemi acidbase",
                                "เคมีแอดมิชชั่น (PAT2)",
                                "อะตอมและตารางธาตุ",
                                "พันธะเคมี",
                                "สมบัติของธาตุและสารประกอบ",
                                "ปริมาณสารสัมพันธ์",
                                "ของแข็ง ของเหลว แก๊ส",
                                "อัตราการเกิดปฏิกิริยาเคมี"
                        });
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
