package dad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServletLoginActuador extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private List<String> actuadores;
	
	public void init() throws ServletException{
		actuadores=new ArrayList<>();
		super.init();
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String user=req.getParameter("nombre");
		if(actuadores.contains(user)) {
			response(res,"login ok");
		}else {
			response(res,"invalid login");
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
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		BufferedReader reader=req.getReader();
		
		Gson gson=new Gson();
		Actuador user=gson.fromJson(reader, Actuador.class);
		if(!user.getNombre().equals("")) {
			actuadores.add(user.getNombre());
			res.getWriter().println(gson.toJson(user));
			res.setStatus(201);
		}else {
			res.setStatus(300);
			response(res,"Wrong sensor name");
		}
		
	}protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    BufferedReader reader = req.getReader();
	    
	    Gson gson = new Gson();
		Actuador user = gson.fromJson(reader, Actuador.class);
		if (!user.getNombre().equals("")
				&& actuadores.contains(user.getNombre())) {
			actuadores.remove(user.getNombre());
			resp.getWriter().println(gson.toJson(user));
			resp.setStatus(201);
		}else{
			resp.setStatus(300);
			response(resp, "Wrong user and password");
		}
	   
	}

}
