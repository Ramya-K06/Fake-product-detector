import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import org.json.JSONObject;

public class VerifyServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        JSONObject json = new JSONObject(sb.toString());
        String serialCode = json.getString("serialCode");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/fake_product_db","root","");

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM products WHERE serial_code=?");
            ps.setString(1, serialCode);
            ResultSet rs = ps.executeQuery();

            String result;
            if(rs.next()) {
                result = "✅ Genuine Product";
            } else {
                result = "❌ Fake Product";
            }

            // Save report
            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO reports (serial_code, result) VALUES (?, ?)");
            ps2.setString(1, serialCode);
            ps2.setString(2, result);
            ps2.executeUpdate();

            JSONObject res = new JSONObject();
            res.put("result", result);
            out.print(res.toString());

        } catch(Exception e) {
            e.printStackTrace();
            out.print("{\"result\":\"Error\"}");
        }
    }
}
