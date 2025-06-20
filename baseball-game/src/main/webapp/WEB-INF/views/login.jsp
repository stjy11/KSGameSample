<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/_header.jsp" %>


<body>
<header>
	<h1>会員認証</h1>
</header>
    <div class="login-container">
        <div class="content">
            <form action="${pageContext.request.contextPath}/login" method="post">

                <div class="input-group mb-3">
                    <label class="form-label" for="memberId">会員ID</label>
                    <input type="text" id="memberId" class="form-control" name="memberId" maxlength="6" required>
                </div>

                <div class="input-group">
                    <label class="form-label" for="memberPassword">パスワード</label>
                    <input type="password" id="memberPassword" class="form-control" name="memberPassword" maxlength="8" required>
                </div>

                <button type="submit" class="btn btn-primary login-btn">ログイン</button>
                <div class="login-error-message">
                    <c:if test="${not empty errorMessage}">
                        <p>${errorMessage}</p>
                    </c:if>
                    <c:if test="${empty errorMessage}">
                    
                        </c:if>
                </div>
                
            </form>
        </div>
    </div>
</body>
</html>