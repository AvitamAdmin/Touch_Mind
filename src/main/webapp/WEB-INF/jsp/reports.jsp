<%@ include file="include.jsp" %>
<div class="main-content">
                <section class="main-content-section">
                <c:forEach var="mapData" items="${reportfiles}" varStatus="status">
                        <h5 style="background-color:beige;font-weight:bold;"> ${mapData.key} </h5>
                        <table class="table">
                        <thead>
                        <tr>
                        <th scope="col">#</th>
                        <th scope="col">File Name</th>
                        <th scope="col">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="fileName" items="${mapData.value}" varStatus="loop">
                        <tr>
                        <th scope="row">${loop.index + 1}</th>
                        <td> ${fileName}</td>
                        <td><a href ="/reports/${fileName}"> view </a></td>
                        </tr>
                        </c:forEach>
                         </tbody>
                        </table>
                        <hr />
                </c:forEach>
                <c:if test="${empty reportfiles}">
                      No Reports found
                </c:if>
                </section>
                </div>


