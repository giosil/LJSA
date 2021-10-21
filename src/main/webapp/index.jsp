<%@ page import="org.dew.ljsa.backend.sched.LJSAScheduler" %>
<!DOCTYPE html>
<html>
  <head>
    <title>LJSA <%= LJSAScheduler.getVersion() %> - Light Java Scheduler Application</title>
    <link rel="stylesheet" href="css/ljsa.css">
  </head>
  <body>
    <h3>LJSA <%= LJSAScheduler.getVersion() %> - Light Java Scheduler Application</h3>
    <hr />
    <p><strong>Status</strong>: <%= LJSAScheduler.getStatus() %></p>
    <br />
    <hr />
    <div class="footer">
    </div>
</body>
</html>