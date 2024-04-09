package dad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletLogin extends HttpServlet {
	
	private static final long serialVersionUID=4837574;
	
	private Map<String, String> userPass;
	
	public void init() throws ServletException{
		userPass=new HashMap<>();
		userPass.put("jp", "123");
		super.init();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String user=req.getParameter("user");
		String pass=req.getParameter("password");
		if(userPass.containsKey(user) && userPass.get(user).equals(pass)) {
			response(res,"login ok");
		}else {
			response(res,"invalid login");
		}
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		BufferedReader reader=req.getReader();
		
		Gson gson=new Gson();
		User user=gson.fromJson(reader, User.class);
		if(!user.getPassword().equals("")&& !user.getUser().equals("")) {
			userPass.put(user.getUser(), user.getPassword());
			res.getWriter().println(gson.toJson(user));
			res.setStatus(201);
		}else {
			res.setStatus(300);
			response(res,"Wrong user and password");
		}
		
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
		User user = gson.fromJson(reader, User.class);
		if (!user.getPassword().equals("") && !user.getUser().equals("") 
				&& userPass.containsKey(user.getUser()) 
				&& userPass.get(user.getUser()).equals(user.getPassword())) {
			userPass.remove(user.getUser());
			resp.getWriter().println(gson.toJson(user));
			resp.setStatus(201);
		}else{
			resp.setStatus(300);
			response(resp, "Wrong user and password");
		}
	   
	}
	
	private void response(HttpServletResponse resp, String msg) throws IOException {
		PrintWriter out=resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<t1>"+msg+"</t1>");
		out.println("</body>");
		out.println("</html>");
	}

}
