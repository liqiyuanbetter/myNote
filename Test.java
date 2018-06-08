package im.qingtui.sa.attendance.schedule.tasks;

import im.qingtui.sa.attendance.schedule.common.constants.OptionConstants;
import im.qingtui.sa.attendance.schedule.common.utils.CommonUtils;
import im.qingtui.sa.attendance.schedule.dao.mapper.RuleReplaceMapper;
import im.qingtui.sa.attendance.schedule.mail.MailInitor;
import im.qingtui.sa.attendance.schedule.mail.MailUtilImpl;
import im.qingtui.sa.attendance.schedule.manager.InitializeManager;
import im.qingtui.sa.attendance.schedule.manager.DeleteTempManager;
import im.qingtui.sa.attendance.schedule.manager.RuleReplaceManager;
import im.qingtui.sa.attendance.schedule.manager.TimingUpdateManager;
import im.qingtui.sa.attendance.schedule.model.AttendanceStatistic;
import im.qingtui.sa.attendance.schedule.model.GroupConfigNormal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * test
 *
 * @author cailun
 */
public class Test {

    @Resource
    MailInitor mailInitor;

    @Resource
    private RuleReplaceManager ruleReplaceManager;

    @Resource
    private InitializeManager initializeManager;

    @Resource
    private DeleteTempManager deleteTempManager;

    @Resource
    private TimingUpdateManager timingUpdateManager;

    @Resource
    private RuleReplaceMapper mapper;

    @org.junit.Test
    public void commonUtilTest() {
        System.out.println(CommonUtils.isHHmmFormat("18:00"));
    }

    public static void main(String[] args) {
        String s = "74745868489523242";
        for (int i = 0; i < 20; i++) {
            s += s;
        }
        long b1 = System.nanoTime();
        String r1 = bigNumberAdd(s, s);
        long e1 = System.nanoTime();

        long b2 = System.nanoTime();
        BigInteger bigInteger = new BigInteger(s);
        BigInteger r2 = bigInteger.add(new BigInteger(s));
        long e2 = System.nanoTime();

        System.out.println("r1 = " + r1 + ", time: " + (e1 - b1));
        System.out.println("r2 = " + r2 + ", time: " + (e2 - b2));

        System.out.println(r1.equals(r2.toString()));
    }

    /**
     * 大数的乘法
     * @param n1
     * @param n2
     * @return
     */
    public static String bigNumberMultiply(String n1, String n2) {
        String biggerNumber = (n1.length() >= n2.length()) ? n1 : n2;
        String smallerNumber = biggerNumber.equals(n1) ? n2 : n1;
        String[] addNumbers = new String[smallerNumber.length()];

        for (int i = smallerNumber.length() - 1; i >=0; i--) {
            int sMul = Integer.valueOf(String.valueOf(smallerNumber.charAt(i)));
            int lastNumber = 0;
            String perAddNumber = "";

            for (int j = biggerNumber.length() - 1; j >=0; j--) {
                int bMul = Integer.valueOf(String.valueOf(biggerNumber.charAt(j)));
                int product = sMul * bMul + lastNumber;
                String strProduct = String.valueOf(product);
                int r;
                if (strProduct.length() == 2) {
                    // 第二位
                    r = Integer.valueOf(strProduct.substring(1, 2));
                    lastNumber = Integer.valueOf(strProduct.substring(0, 1));
                } else {
                    r = product;
                    lastNumber = 0;
                }
                perAddNumber = r + perAddNumber;
            }
            addNumbers[smallerNumber.length() - i - 1] = (lastNumber == 0 ? "" : lastNumber) + perAddNumber;
        }

        for (int i = 0; i < addNumbers.length; i++) {
            for (int j = addNumbers.length - i; j < addNumbers.length; j++) {
                addNumbers[i] = addNumbers[i] + "0";
            }
        }
        return bigNumberAdd(addNumbers);
    }

    /**
     * 大数加法, 接收多个参数
     * @param numbers
     * @return
     */
    public static String bigNumberAdd(String... numbers) {
        int arrayLength = numbers.length;
        if (arrayLength == 1) {
            return numbers[0];
        }

        String lastResult = "0";
        for (int i = 0; i < arrayLength; i++) {
            lastResult = bigNumberAdd(lastResult, numbers[i]);
        }

        return lastResult;
    }

    /**
     * 大数加法, 接收两个参数
     * @param n1
     * @param n2
     * @return
     */
    public static String bigNumberAdd(String n1, String n2) {
        if (n1.equals("0")) {
            return n2;
        }
        if (n2.equals("0")) {
            return n1;
        }

        int maxLength = Math.max(n1.length(), n2.length());
        StringBuilder result = new StringBuilder("");

        while (n1.length() < maxLength || n2.length() < maxLength) {
            if (n1.length() < maxLength) {
                n1 = "0" + n1;
            }
            if (n2.length() < maxLength) {
                n2 = "0" + n2;
            }
        }
        // 是否需要进位
        boolean GEQ10 = false;
        for (int i = maxLength - 1; i >=0; i--) {
            int c1 = Integer.valueOf(String.valueOf(n1.charAt(i)));
            int c2 = Integer.valueOf(String.valueOf(n2.charAt(i)));

            int t = c1 + c2 + (GEQ10 ? 1 : 0);
            GEQ10 = (t >= 10);
            if (GEQ10) {
                t -= 10;
            }

            result.append(t);
        }

        if (GEQ10) {
            result.append("1");
        }

        return result.reverse().toString();
    }

    @org.junit.Test
    public void t() {
        ArrayList<String> info = new ArrayList<String>();
        String error = "测试中文能不能正常显示"
            + "testsdfsdfdsfdsfdsfsdfsdfsdfsdfsdfsdfsdfsdfsdf1"
            + "testsdfsdfdsfdsfdsfsdfsdfsdfsdfsdfsdfsdfsdfsdf1";
        info.add(CommonUtils.wrapperError(error));
        info.add("test2");
        MailUtilImpl mailUtil = new MailUtilImpl();
        String[] emails = CommonUtils.getSysValueByKey("toEmailAddress").split(",");

        for (String email : emails) {
            mailUtil.sendSimpleMail(info, email, "标准考勤组缺勤统计详情", mailInitor);
        }
    }

    @org.junit.Test
    public void test() {
        String value = CommonUtils.getSysValueByKey("toEmailAddress");
        String[] emails = value.split(",");
        System.out.println(emails.length);
        for (String email : emails) {
            System.out.println(email);
        }
    }

    @org.junit.Test
    public void testMapper() {
        /*List<GroupConfigNormal> configNormals = initializeManager.selectLastNormalConfigById("29d06ccf893c428c9245ed47dd7db97f");
        CommonUtils.traverseList(configNormals);*/

        /*List<GroupConfigCycle> cycles = initializeManager.selectLastCycleConfigById("lunbankaoqinzu");
        CommonUtils.traverseList(cycles);*/
        int i = deleteTempManager.deleteStatisticTmpData(516959493875l);
        System.out.println(i);
    }

    @org.junit.Test
    public void testTimingUpdateManager() {
        List<AttendanceStatistic> list = timingUpdateManager.selectStatisticLaterThan(1518105600000l);
        System.out.println("---" + list.get(0).getGmtSignOut());
    }

    @org.junit.Test
    public void testRuleReplaceManager() {
        List<GroupConfigNormal> groupConfigNormals = ruleReplaceManager.selectAllNewNormalConfig();
        System.out.println(groupConfigNormals.size());
    }

    @org.junit.Test
    public void testabsenteeismManager() {
        AttendanceStatistic statistic = new AttendanceStatistic();
        statistic.setInOriginalStatus(OptionConstants.STATUS_LACKONDUTY);
        statistic.setOutOriginalStatus(OptionConstants.STATUS_LACK_OFFDUTY);
        int i = initializeManager.insertStatistic(statistic);
    }

    @org.junit.Test
    public void jdkTest() {
        System.out.println(CommonUtils.getTodayWeekDay());

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       /* GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(1516921200000l);
        Date time = calendar.getTime();
        System.out.println(time);*/
        /*Date date = new Date();
        long l = CommonUtils.get0PointTimeMills(date.getTime());
        long l2 = CommonUtils.getCurrentDay0PointTimeMills();
        System.out.println("l1 = " + l);
        System.out.println("l2 = " + l2);*/
        // System.out.println(CommonUtils.getCurrentDay0PointTimeMills());

        /*GregorianCalendar currentCalendar = new GregorianCalendar();
        currentCalendar.setTimeInMillis(CommonUtils.getCurrentDay0PointTimeMills());
        currentCalendar.add(Calendar.DAY_OF_MONTH, -2);

        System.out.println(sdf.format(currentCalendar.getTime()));
        System.out.println(currentCalendar.getTimeInMillis());*/
    }

    @org.junit.Test
    public void testEquals() {
        String a1 = "{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"count\":10,\"size\":10,\"total\":41,\"workflows\":[{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519980412843,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302164652531\",\"updateTime\":1519980412843,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519980371787,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302164611530\",\"updateTime\":1519980371787,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519980324844,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302164524529\",\"updateTime\":1519980324844,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519979970976,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302163930528\",\"updateTime\":1519979970976,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519979948184,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302163908527\",\"updateTime\":1519979948184,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519977084604,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302155124525\",\"updateTime\":1519977084604,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519976421758,\"flowName\":\"请假-赛迪信息\",\"flowNo\":\"20180302154021524\",\"updateTime\":1519976421758,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"3af9426129064cb0999e119bc359d844\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519976114684,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302153514523\",\"updateTime\":1519976121355,\"status\":3,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519972824584,\"flowName\":\"超级超级现金\",\"flowNo\":\"20180302144024521\",\"updateTime\":1519972831066,\"status\":3,\"submitter\":\"ge\",\"templateid\":\"7585b32ecc1b402ea0d76378ce823787\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519972752193,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302143912520\",\"updateTime\":1519972759031,\"status\":3,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true}]}";
        String b1 = "{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"count\":10,\"size\":10,\"total\":41,\"workflows\":[{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519980412843,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302164652531\",\"updateTime\":1519980412843,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519980371787,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302164611530\",\"updateTime\":1519980371787,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519980324844,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302164524529\",\"updateTime\":1519980324844,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519979970976,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302163930528\",\"updateTime\":1519979970976,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519979948184,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302163908527\",\"updateTime\":1519979948184,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519977084604,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302155124525\",\"updateTime\":1519977084604,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519976421758,\"flowName\":\"请假-赛迪信息\",\"flowNo\":\"20180302154021524\",\"updateTime\":1519976421758,\"status\":1,\"submitter\":\"ge\",\"templateid\":\"3af9426129064cb0999e119bc359d844\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519976114684,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302153514523\",\"updateTime\":1519976121355,\"status\":3,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519972824584,\"flowName\":\"超级超级现金\",\"flowNo\":\"20180302144024521\",\"updateTime\":1519972831066,\"status\":3,\"submitter\":\"ge\",\"templateid\":\"7585b32ecc1b402ea0d76378ce823787\",\"read\":true},{\"code\":0,\"result\":null,\"message\":null,\"errorCode\":0,\"data\":null,\"createTime\":1519972752193,\"flowName\":\"斤斤计较\",\"flowNo\":\"20180302143912520\",\"updateTime\":1519972759031,\"status\":3,\"submitter\":\"ge\",\"templateid\":\"6dd76c6c15834a578104958987c0f829\",\"read\":true}]}";

        System.out.println(a1.equals(b1));
    }

}