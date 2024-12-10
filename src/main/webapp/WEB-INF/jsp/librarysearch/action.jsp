<div class="row">
    <div class="col-sm-12">
        <p style="font-size:22px;font-weight:bolder;">Actions</p>
    </div>
</div>
  <div class="row">
    <div class="col-sm-12">
         <table id="tableData3" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%" data-tableName="Stock Data">
              <thead>
                  <tr>
                      <th class="th-sm"><input class='chall' id="checkAllAction" type='checkbox'></th>
                      <th class="th-sm">action</th>
                      <th class="th-sm">system</th>
                      <th class="th-sm">library</th>
                      <th class="th-sm">media</th>
                      <th class="th-sm">remarks</th>
                      <th class="th-sm">system path</th>
                      <th class="th-sm">subsidiary</th>
                  </tr>
            </thead>
            <tbody>
                <c:forEach items="${actionsList}" var="model">
                  <tr id="${model.id}">
                      <td><img id="pinImg${model.id}" src="${contextPath}/images/pin.png" style="display:none;width:20px;height:20px;margin-bottom:10px;margin-left:-8px;" /><br/><input class='ch' id="checkAction${model.id}" type='checkbox'></td>
                      <td class="td-sm">${model.id}</td>
                      <td class="td-sm">${model.system.shortDescription}</td>
                      <c:set var="hasValue" value="false"></c:set>
                        <c:forEach var="mapData" items="${actionLibMap}" varStatus="status">
                          <c:if test="${mapData.key == model.id}">
                              <c:set var="hasValue" value="true"></c:set>
                              <td class="td-sm">
                              <c:forEach var="library" items="${mapData.value}" varStatus="status">
                                <a class="nav-link" style="color:blue;" href="#" onclick="javascript:fire_ajax_submit('/admin/library/edit?id=${library.id}')">${library.id}</a>
                                <br/>
                              </c:forEach>
                              </td>
                          </c:if>
                        </c:forEach>
                        <c:if test="${hasValue == false}" >
                          <td class="td-sm"></td>
                        </c:if>
                      <c:set var="hasValueMedia" value="false"></c:set>
                      <c:forEach var="mapData" items="${actionMediaMap}" varStatus="status">
                        <c:if test="${mapData.key == model.id}">
                            <c:set var="hasValueMedia" value="true"></c:set>
                            <td class="td-sm">
                            <c:forEach var="media" items="${mapData.value}" varStatus="status">
                              <c:choose>
                                  <c:when test="${fn:contains(media.shortDescription, '.png') || fn:contains(media.shortDescription, '.jpeg')
                                  || fn:contains(media.shortDescription, '.jpg')}">
                                       <a style="color:blue;" data-lightbox="image-1" href='${contextPath}/files?filename=${media.shortDescription}'>${media.shortDescription}</a>
                                   </c:when>
                                   <c:otherwise>
                                       <a style="color:blue;" href='${contextPath}/files?filename=${media.shortDescription}'>${media.shortDescription}</a>
                                   </c:otherwise>
                              </c:choose>
                              <br/>
                            </c:forEach>
                            </td>
                        </c:if>
                      </c:forEach>
                       <c:if test="hasValueMedia == false">
                            <td class="td-sm"></td>
                       </c:if>
                      <td class="td-sm"><form:input type="text" path="dummyRemarks" style="height:40px;" id="remarks-${model.id}" value="${model.remarks}" class="inputbox-cheil-full" placeholder="Remarks"/></td>

                      <div id="currentLibId" style="display:none">${editForm.id}</div>
                      <td class="td-sm" id="text-only">${model.systemPath}</td>
                      <td class="td-sm">${model.subsidiaries}</td>
                   </tr>

               </c:forEach>
           </tbody>
         </table>
    </div>
  </div>
<br/>
<br/>
<%@ include file="tableActions2.jsp" %>