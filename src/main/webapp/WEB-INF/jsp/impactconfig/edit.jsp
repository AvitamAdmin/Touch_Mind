<%@ include file="../include.jsp" %>
<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
  </div>
  <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/impactConfig/add" modelAttribute="editForm">
    <div class="row">
      <div class="col-sm-12">
        <div class="dt-buttons">
          <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/impactConfig')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
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
      <div class="col-sm-4"></div>
      <div class="col-sm-4"></div>
    </div>
   <br/><br/>

   <c:forEach items="${editForm.labels}" var="label" varStatus="loop">
       <div class="row" id="label${loop.index}">
               <div class="col-sm-4" style="margin-top:8px;">
                    <select class="cheil-select" name="labels[${loop.index}].labels" id="selectpicker-${loop.index}" multiple>
                       <option value="">Select label</option>
                       <c:forEach items="${labels}" var="child">
                           <c:choose>
                               <c:when test="${fn:contains( label.labels, child ) }">
                                 <option value="${child}" selected>${child}</option>
                               </c:when>
                               <c:otherwise>
                                  <option value="${child}" >${child}</option>
                               </c:otherwise>
                           </c:choose>
                       </c:forEach>
                    </select>
               </div>
               <div class="col-sm-3">
                   <form:input path="labels[${loop.index}].impact" class="inputbox-cheil-small" placeholder="Enter impact" required="required" />
                   <span>Enter impact</span>
               </div>
               <div class="col-sm-3">
                  <form:input path="labels[${loop.index}].multiplier" class="inputbox-cheil-small" placeholder="Enter multiplier" required="required" />
                  <span>Enter multiplier</span>
              </div>
               <div class="col-sm-2">
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
                    <button class="btn btn-primary add-label-more" type="button"><i class="glyphicon glyphicon-add"></i>Add label</button>
                </div>
            </div>
      </div>
   </div>
  </form:form>
<script type="text/javascript">
      var count = $('#existingLabelCount').val();
            for(i=0;i<count;i++){
              var sitesData = new Choices('#selectpicker-'+i, {
                           removeItemButton: true,
                           maxItemCount:-1,
                           searchResultLimit:20,
                           renderChoiceLimit:-1
            });
            }
    $(document).ready(function() {

            $(".add-label-more").click(function(){
                var html = '<div class="row" id="label'+count+'"><div class="col-sm-4" style="margin-top:8px;"><select class="cheil-select" name="labels['+count+'].labels" id="selectpicker-'+count+'" multiple><option value="">Select label</option><c:forEach items="${labels}" var="child"><option value="${child}" >${child}</option></c:forEach></select></div><div class="col-sm-3"><input name="labels['+count+'].impact" class="inputbox-cheil-small" placeholder="Enter impact" required="required" /><span>Enter impact</span></div><div class="col-sm-3"><input name="labels['+count+'].multiplier" class="inputbox-cheil-small" placeholder="Enter multiplier" required="required" /><span>Enter multiplier</span></div><div class="col-sm-2"><img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeToolkitRow('+count+')" src="${contextPath}/images/remove.png" /></div></div><br/><br/>';
                $(".after-add-label-more").before(html);
                var sitesData3 = new Choices('#selectpicker-'+count, {
                                         removeItemButton: true,
                                         maxItemCount:-1,
                                         searchResultLimit:20,
                                         renderChoiceLimit:-1,
                                         placeholderValue: "Select Label"
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