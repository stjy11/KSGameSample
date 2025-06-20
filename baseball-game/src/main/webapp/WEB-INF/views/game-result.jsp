<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/_header.jsp" %>
<body>
<header>
	<h1>ゲーム結果</h1>
</header>
    <div class="game-result-container">

        <p>
            <c:out value="${gameResult.resultMessage}" />
        </p>

        <p>正解は&nbsp;&nbsp;<span><c:out value="${gameResult.finalHiddenNumber}" /></span>！</p>
 

        <p>獲得ポイント&nbsp;
            <span>
                <c:choose>
                    <c:when test="${gameResult.gotPoints == 0}">
                        0
                    </c:when>
                    <c:otherwise>
                        <c:out value="${gameResult.gotPoints}" />
                    </c:otherwise>
                </c:choose>
            </span>
        </p>

        <button onclick="window.close()" class="btn btn-primary">CLOSE</button>
    </div>

</body>
</html>