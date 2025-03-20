<%@ include file="../include.jsp" %>
<div class="main-content">

  <div class="row">
    <div class="col-sm-12">
         <table id="tableData2" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <input type="hidden" id="rowSelectorIdMedia" name="rowSelectorIdMedia" value="">
              <thead>
                    <tr>
                      <th class="th-sm">ID</th>
                      <th class="th-sm">file</th>
                      <th class="th-sm">creator</th>
                      <th class="th-sm">creation time</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${medias}" var="model">
                    <tr id="${model.id}">
                        <td class="td-sm">${model.id}</td>
                        <c:choose>
                            <c:when test="${fn:contains(model.shortDescription, '.png') || fn:contains(model.shortDescription, '.jpeg')
                            || fn:contains(model.shortDescription, '.jpg')}">
                                 <td class="td-sm"><a style="color:blue;" data-lightbox="image-1" href='${contextPath}/files?filename=${model.shortDescription}'>${model.shortDescription}</a></td>
                             </c:when>
                             <c:otherwise>
                                 <td class="td-sm"><a style="color:blue;" href='${contextPath}/files?filename=${model.shortDescription}'>${model.shortDescription}</a></td>
                             </c:otherwise>
                        </c:choose>
                        <td class="td-sm">${model.creator}</td>
                        <td class="td-sm">${model.creationTime}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</div>
<%@ include file="tableActions1.jsp" %>