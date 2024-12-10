<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/dashboardProfile/add" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/dashboardProfile')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
        </div>
      </div>
    </div>
    <%@ include file="../commonFields.jsp" %>
    <form:input path="recordId" class="inputbox-cheil-small" placeholder="Record Id" style="display:none" type="hidden"/>
    <div class="row">
      <div class="col-sm-4">
        <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter the unique identifier" required="required" />
        <span>Enter Id</span>
        <form:errors path="identifier" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4">
        <form:input path="description" class="inputbox-cheil-long" placeholder="Enter Description" />
        <span>Enter Description</span>
        <form:errors path="description" class="text-danger"></form:errors>
      </div>
      <div class="col-sm-4"></div>
    </div>
   <br/><br/>

   <c:forEach items="${editForm.labels}" var="label" varStatus="loop">
       <div class="row" id="label${loop.index}">
               <div class="col-sm-4">
                    <select class="cheil-select toolkitNode" name="labels[${loop.index}].parent" id="parentSelectpicker" >
                       <option value="">Select label</option>
                       <c:forEach items="${labels}" var="child">
                           <c:choose>
                               <c:when test="${label.parent eq child}">
                                 <option value="${child}" selected>${child}</option>
                               </c:when>
                               <c:otherwise>
                                  <option value="${child}" >${child}</option>
                               </c:otherwise>
                           </c:choose>
                       </c:forEach>
                    </select>
               </div>
               <div class="col-sm-4">
                   <select class="3col active cheil-select" name="labels[${loop.index}].children" id="selectpicker" multiple>
                      <option value="">Select child labels</option>
                          <c:forEach items="${labels}" var="unselectedChild">
                            <c:forEach items="${label.children}" var="child">
                                  <c:choose>
                                      <c:when test="${unselectedChild eq child}">
                                        <option value="${unselectedChild}" selected>${unselectedChild}</option>
                                      </c:when>
                                      <c:otherwise>
                                         <option value="${unselectedChild}" >${unselectedChild}</option>
                                      </c:otherwise>
                                  </c:choose>
                              </c:forEach>
                          </c:forEach>
                   </select>
               </div>
               <div class="col-sm-4">
                   <img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeToolkitRow('${loop.index}')" src="${contextPath}/images/remove.png" />
               </div>
        </div>
        <br/>
        <br/>
   </c:forEach>
            <div class="input-group control-group after-add-label-more"></div>
            <div class="row">
                <div class="col-sm-12">
                    <input id="existingLabelCount" name="existingLabelCount" value="${existingLabelCount}" style="display:none;">
                    <button class="btn btn-primary add-label-more" type="button"><i class="glyphicon glyphicon-add"></i>Add dashboard label</button>
                </div>
            </div>
      </div>
   </div>
  </form:form>
<script type="text/javascript">
var multipleCancelButton = new Choices('#selectpicker', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1
           });
           var parent = new Choices('#parentSelectpicker', {
                        removeItemButton: true,
                        maxItemCount:-1,
                        searchResultLimit:20,
                        renderChoiceLimit:-1
                      });
    $(document).ready(function() {
      var count = $('#existingLabelCount').val();
            $(".add-label-more").click(function(){
                var html = '<div class="row" id="label'+count+'"><div class="col-sm-4"><select class="3col active cheil-select" name="labels['+count+'].parent" id="parentSelectpicker" ><option value="">Select label</option><c:forEach items="${labels}" var="child"><option value="${child}" >${child}</option></c:forEach></select></div><div class="col-sm-4"><select class="3col active cheil-select" name="labels['+count+'].children" id="selectpicker-'+count+'" multiple><option value="">Select child labels</option><c:forEach items="${labels}" var="unselectedChild"><option value="${unselectedChild}" >${unselectedChild}</option></c:forEach></select></div><div class="col-sm-4"><img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeToolkitRow('+count+')" src="${contextPath}/images/remove.png" /></div></div><br/><br/>';
                $(".after-add-label-more").before(html);
                var sitesData3 = new Choices('#selectpicker-'+count, {
                                         removeItemButton: true,
                                         maxItemCount:-1,
                                         searchResultLimit:20,
                                         renderChoiceLimit:-1
                          });
                count++;
            });
      });
</script>

  <c:if test="${not empty message}">
    <div class="alert alert-danger" role="alert" id="errorMessage">
      <spring:message code="${message}" />
    </div>
  </c:if>
</div>
</div>
</div>