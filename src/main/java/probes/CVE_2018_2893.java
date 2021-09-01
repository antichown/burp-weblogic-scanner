package probes;

import burp.IHttpRequestResponse;
import burp.IScanIssue;
import burp.Utilities;
import burp.WebLogicIssue;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;

public class CVE_2018_2893 extends Probe {
    private static final String NAME = "CVE-2018-2893";
    private static final String SEVERITY = "High";
    private static String detail = "WebLogic has a T3 protocol vulnerability!";

    @Override
    public IScanIssue check(IHttpRequestResponse requestResponse) {
        URL target = Utilities.helpers.analyzeRequest(requestResponse).getUrl();
        Pattern p = Pattern.compile("\\$Proxy[0-9]+");
        String result = "";
        try {
            Socket s = new Socket(target.getHost(), target.getPort());
            s.setSoTimeout(10);

            result = sendT3Payload(s, payloads.get("Activator"));
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return result.contains("weblogic.jms.common.StreamMessageImpl") ? new WebLogicIssue(requestResponse, NAME, DETAIL, SEVERITY) : null;
        if (p.matcher(result).find()) {
            // 和2018-2628相比，过滤了CommonsCollection链，可以用Jdk7u21
            detail = detail + "<br/><br/>" + result;
            return new WebLogicIssue(requestResponse, NAME, detail, SEVERITY);
        } else return null;
    }
}
