package id.co.asyst.amala.getpromobycode.utils;

import com.google.gson.Gson;
import id.co.asyst.commons.utils.constant.CommonsConstants;
import id.co.asyst.commons.utils.formatter.Responses;
import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.rmi.MarshalledObject;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Utils {
	private static final Logger log = LogManager.getLogger(Responses.class);

	private static final Gson gson = new Gson();

	public void validateRequest(Exchange exchange){
		Map<String, Object> requestData = exchange.getProperty("requestdata", HashMap.class);
		String resMsg = "";
		try {
			if (requestData.get("promocode") == null || StringUtils.isEmpty(requestData.get("promocode")))
				resMsg = "promocode is required";
			else if (requestData.get("promotype") == null || StringUtils.isEmpty(requestData.get("promotype")))
				resMsg = "promotype is required";
			else if (requestData.get("total") == null || StringUtils.isEmpty(requestData.get("total")))
				resMsg = "total is required";
		} catch (Exception e) {
			resMsg = "Invalid Input Data";
		}
		exchange.setProperty("resmsg", resMsg);
	}

	public void generateDateTimeNow(Exchange exchange){
		Date date = new Date();
		SimpleDateFormat smdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat smd = new SimpleDateFormat("yyyy-MM-dd");
		String srtDate = smdt.format(date);
		exchange.setProperty("datetimenow",srtDate);
		String srtDateFormat = smd.format(date);
		exchange.setProperty("datenow",srtDateFormat);
	}

	public void calculatediscount(Exchange exchange) {
		List<Map<String, Object>> promocataloglist = exchange.getProperty("promocatalog", List.class);
		Map<String, Object> requestdata = exchange.getProperty("requestdata", Map.class);
		Map<String, Object> promocatalog = promocataloglist.get(0);
		if (promocatalog.get("discounttype").toString().toUpperCase().equals("PERCENTAGE"))
			percentage(Double.parseDouble(promocatalog.get("discount").toString()), Math.ceil(Double.parseDouble(requestdata.get("total").toString())), promocatalog);
		else if (promocatalog.get("discounttype").toString().toUpperCase().equals("MILEAGE"))
			mileage(Math.ceil(Double.parseDouble(promocatalog.get("discount").toString())), Math.ceil(Double.parseDouble(requestdata.get("total").toString())), promocatalog);
		exchange.setProperty("promocatalog", promocatalog);
	}

	public void responseSuccess(Exchange exchange) {
		String[] date = {"startdate","enddate","createddate","updateddate"};
		Map<String, Object> result = exchange.getProperty("promocatalog", Map.class);
		Map<String, Object> response = new HashMap<String, Object>();
		for (int i = 0; i < date.length; i++) {
			for (Map.Entry<String, Object> res : result.entrySet()) {
				if (res.getKey().toString().equals(date[i].toString())) {
					if (res.getValue() != null)
						result.put(date[i], res.getValue().toString());
				}
			}
		}

		response.put(CommonsConstants._SERVICE_IDENTITY_KEY, exchange.getProperty("identity", Map.class));
		response.put(CommonsConstants._RESPONSE_RESULT_KEY, result);
		response.put(CommonsConstants._RESPONSE_STATUS, status(exchange));
		exchange.getOut().setBody(gson.toJson(response));
	}

	private void percentage(Double discount, Double totalreq, Map<String, Object> result) {
		Double totaldiscount = totalreq * (discount/100);
		Integer td = (int) Math.ceil(totaldiscount);
		Integer tr = (int) Math.ceil(totalreq);
		Integer totalafterdiscount = tr - td;
		result.put("total", tr);
		result.put("totaldiscount", td);
		if (Math.round(Math.ceil(totalafterdiscount)) > 0)
			result.put("totalafterdiscount", totalafterdiscount);
		else
			result.put("totalafterdiscount", Integer.parseInt("0"));
	}

	private void mileage(Double discount, Double totalreq, Map<String, Object> result) 	{
		Double totaldiscount = discount;
		Double totalafterdiscount = totalreq - totaldiscount;
		result.put("total", (int) Math.round(totalreq));
		result.put("totaldiscount", (int) Math.round(totaldiscount));
		if (Math.round(Math.ceil(totalafterdiscount)) > 0)
			result.put("totalafterdiscount", (int) Math.round(Math.ceil(totalafterdiscount)));
		else
			result.put("totalafterdiscount", Integer.parseInt("0"));
	}

	@SuppressWarnings("unused")
	private Map<String,Object> status(Exchange exchange){
		Map<String,Object> status=new HashMap<String,Object>();
		String rescode=exchange.getProperty("rescode",String.class);
		String resdesc=exchange.getProperty("resdesc",String.class);
		String resmsg=exchange.getProperty("resmsg",String.class);


		status.put(CommonsConstants._RESPONSE_CODE,rescode);
		status.put(CommonsConstants._RESPONSE_DESC,resdesc);
		status.put(CommonsConstants._RESPONSE_MSG,resmsg);

		return status;
	}
}
