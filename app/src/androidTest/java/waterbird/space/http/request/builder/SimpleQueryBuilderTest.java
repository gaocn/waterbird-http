package waterbird.space.http.request.builder;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import waterbird.space.http.log.HttpLog;
import waterbird.space.http.request.param.HttpCustomParam;

/**
 * Created by 高文文 on 2016/12/23.
 */
public class SimpleQueryBuilderTest {
    private static final String TAG = "SimpleQueryBuilderTest";

    class TestModel {
        Map<String, String> map = new HashMap<>();
        ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<>();
        Cust cust = new Cust();
        Object[] objects = {23, 23};

        boolean isTestMode = false;

        public TestModel() {
            ArrayList<Integer> arrayList1 = new ArrayList<>();
            ArrayList<Integer> arrayList2 = new ArrayList<>();
            arrayList1.add(101);
            arrayList1.add(102);
            arrayList1.add(103);
            arrayList2.add(201);
            arrayList2.add(201);
            arrayLists.add(arrayList1);
            arrayLists.add(arrayList2);
            map.put("111", "111str");
            map.put("222", "222str");
            map.put("333", "333str");

        }

        class Cust implements HttpCustomParam {

            @Override
            public CharSequence buildValue() {
                HttpLog.d(TAG, "buildValue invoked");
                return "HttpCustomParam：3243";
            }
        }
    }

    @Test
    public void buildSecondaryValue() throws Exception {
        /*
            {arrayLists=[[101,102,103],[201,201]],}{cust=HttpCustomParam%EF%BC%9A3243,}{isTestMode=false,}{map={111=111str,333=333str,222=222str},}{objects=[23,23]}
         */
        SimpleQueryBuilder simpleQueryBuilder = new SimpleQueryBuilder();
        HttpLog.d(TAG, simpleQueryBuilder.buildSecondaryValue(new TestModel()).toString());
    }

}