<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/environment/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/environment')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="ajaxformSubmit('editForm');" aria-controls="tableData" type="submit" title="Save">Save</button>
                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-4">
                <form:input path="recordId" class="inputbox-cheil-small" placeholder="PK" readonly="true" type="hidden"/>
                <form:input path="identifier" class="inputbox-cheil-small" placeholder="Enter Id" required="required" />
                <span>Enter Id</span>
                <form:errors path="identifier" class="text-danger"></form:errors>
            </div>
            <div class="col-sm-4">
                <form:input path="shortDescription" class="inputbox-cheil-long" placeholder="Enter Description" />
                <span>Enter Description</span>
                <form:errors path="shortDescription" class="text-danger"></form:errors>
            </div>
        </div>
        <br/><br/>
        <c:forEach items="${editForm.configs}" var="configs" varStatus="loop">
                <div class="row" id="environment${loop.index}">
                        <div class="col-sm-6">
                            <p style="font-weight: bold;font-size:20px;">Configure Url</p>
                            <c:choose>
                                 <c:when test="${configs.waitBeforeUrl}">
                                     <c:set var="varWaitBeforeUrlChecked" value="checked"></c:set>
                                 </c:when>
                                 <c:otherwise>
                                     <c:set var="varWaitBeforeUrlChecked" value=""></c:set>
                                 </c:otherwise>
                            </c:choose>
                            <c:choose>
                                 <c:when test="${configs.waitAfterUrl}">
                                     <c:set var="varWaitAfterUrlChecked" value="checked"></c:set>
                                 </c:when>
                                 <c:otherwise>
                                     <c:set var="varWaitAfterUrlChecked" value=""></c:set>
                                 </c:otherwise>
                            </c:choose>
                            <input type="text" class="inputbox-cheil-mini" name="configs[${loop.index}].url" value="${configs.url}" title="Enter URL for environment" placeholder="Enter URL for environment"/>&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs[${loop.index}].waitBeforeUrl"  title="Wait before url" placeholder="Wait before url" ${varWaitBeforeUrlChecked} />&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs[${loop.index}].waitAfterUrl"  title="Wait after Url" placeholder="wait after url" ${varWaitAfterUrlChecked} />
                            <p style="font-weight: bold;font-size:20px;margin-top:10px;">Short description</p><input type="text" class="inputbox-cheil-small" name="configs[${loop.index}].shortDescription" value="${configs.shortDescription}" title="Short description" placeholder="Short description"/>
                            <c:choose>
                                 <c:when test="${configs.waitBeforeClick}">
                                     <c:set var="varWaitBeforeClickChecked" value="checked"></c:set>
                                 </c:when>
                                 <c:otherwise>
                                     <c:set var="varWaitBeforeClickChecked" value=""></c:set>
                                 </c:otherwise>
                            </c:choose>
                            <c:choose>
                                 <c:when test="${configs.waitAfterClick}">
                                     <c:set var="varWaitAfterClickChecked" value="checked"></c:set>
                                 </c:when>
                                 <c:otherwise>
                                     <c:set var="varWaitAfterClickChecked" value=""></c:set>
                                 </c:otherwise>
                            </c:choose>
                            <p style="font-weight: bold;font-size:20px;margin-top:10px;">UI action element (Optional)</p><input type="text" class="inputbox-cheil-mini" name="configs[${loop.index}].actionElement" value="${configs.actionElement}" title="UI selector should be By xPath, or By Id or By selector" placeholder="Enter UI element selector "/>&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs[${loop.index}].waitBeforeClick"  title="Wait before submit" placeholder="wait before submit" ${varWaitBeforeClickChecked} />&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs[${loop.index}].waitAfterClick"  title="Wait after submit" placeholder="wait after submit" ${varWaitAfterClickChecked} />
                        </div>
                        <div class="col-sm-5">
                            <p style="font-weight: bold;font-size:20px;">Configure credentials (Optional) </p>
                            <div id="div1" style="height: 200px;">
                                <div id="div2" style="height: inherit; overflow: auto; border:1px solid gray;">
                                    <div class="row">
                                        <div class="col-sm-4" style="margin-top:15px;">
                                            <b style="padding-left: 10px;">Login user</b>
                                         </div>
                                         <div class="col-sm-6">
                                            <input type="text" class="inputbox-cheil-mini" name="configs[${loop.index}].loginName" value="${configs.loginName}" title="Enter login name" placeholder="Enter login name"/>&nbsp;&nbsp;&nbsp;
                                            <input type="text" class="inputbox-cheil-mini" name="configs[${loop.index}].loginNameUiSelector" value="${configs.loginNameUiSelector}" title="Enter UI selector (ID/CSS/Xpath)" placeholder="Enter UI selector (ID/CSS/Xpath)" />
                                         </div>
                                    </div><br/>
                                    <div class="row">
                                        <div class="col-sm-4" style="margin-top:15px;">
                                            <b style="padding-left: 10px;">Login password</b>
                                        </div>
                                        <div class="col-sm-6">
                                            <input type="password" class="inputbox-cheil-mini" name="configs[${loop.index}].loginPassword" value="${configs.loginPassword}" title="Enter login password " placeholder="Enter login password "/>&nbsp;&nbsp;&nbsp;
                                            <input type="text" class="inputbox-cheil-mini" name="configs[${loop.index}].loginPasswordSelector" value="${configs.loginPasswordSelector}" title="Enter UI selector (ID/CSS/Xpath)" placeholder="Enter UI selector (ID/CSS/Xpath)" />
                                        </div>
                                    </div><br/>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-1"><img style="width:32px;height:32px;margin-left:25px;margin-top:100px;" onclick="javascript:removeRow(${loop.index})" src="${contextPath}/images/remove.png" /></div>
                </div>
                <br/><br/>
            </c:forEach>
            <input id="existingEnvironmentCount" name="existingEnvironmentCount" value="${existingEnvironmentCount}" style="display:none;">
            <div class="input-group control-group after-add-input-more">
            <button class="btn btn-primary add-input-more" type="button"><i class="glyphicon glyphicon-add"></i>Add environment configuration</button>
        </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
    </div
  </div>
</div>
<script type="text/javascript">
      function removeRow(countVal) {
        $('#environment'+countVal).remove();
      }
        $(document).ready(function() {
        var multipleCancelButton = new Choices('#selectpicker', {
                  removeItemButton: true,
                  maxItemCount:-1,
                  searchResultLimit:20,
                  renderChoiceLimit:-1
                });

            var count = $('#existingEnvironmentCount').val();
            $(".add-input-more").click(function(){
                var html = '<div class="row" id="environment"'+count+'"><div class="col-sm-5"><p style="font-weight: bold;font-size:20px;">Configure Url</p><input type="text" class="inputbox-cheil-mini" name="configs['+count+'].url" value="" title="Enter the environment URL" placeholder="Enter the environment URL"/>&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs['+count+'].waitBeforeUrl"  title="Wait before url" placeholder="Wait before url" />&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs['+count+'].waitAfterUrl"  title="Wait after Url" placeholder="wait after url" /><p style="font-weight: bold;font-size:20px;margin-top:10px;">Short description</p><input type="text" class="inputbox-cheil-small" name="configs['+count+'].shortDescription" value="" title="Short description" placeholder="Short description"/><p style="font-weight: bold;font-size:20px;margin-top:10px;">UI action element (Optional)</p><input type="text" class="inputbox-cheil-mini" name="configs['+count+'].actionElement" value="" title="UI selector should be By xPath, or By Id or By selector" placeholder="Enter UI element selector "/>&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs['+count+'].waitBeforeClick"  title="Wait before submit" placeholder="wait before submit" />&nbsp;&nbsp;<input type="checkbox" style="width: 20px;transform : scale(1.5);" name="configs['+count+'].waitAfterClick"  title="Wait after submit" placeholder="wait after submit "/></div><div class="col-sm-5"><p style="font-weight: bold;font-size:20px;">Configure credentials (Optional) </p><div id="div1" style="height: 200px;"><div id="div2" style="height: inherit; overflow: auto; border:1px solid gray;"><div class="row"><div class="col-sm-4" style="margin-top:15px;"><b style="padding-left: 10px;">User login</b></div><div class="col-sm-8"><input type="text" class="inputbox-cheil-mini" name="configs['+count+'].loginName" value="" title="Enter login name" placeholder="Enter login name" class="inputbox-cheil-mini" />&nbsp;&nbsp;&nbsp;<input type="text" class="inputbox-cheil-mini" name="configs['+count+'].loginNameUiSelector" value="" title="Enter UI selector (ID/CSS/Xpath)" placeholder="Enter UI selector (ID/CSS/Xpath)" /></div></div><br/><div class="row"><div class="col-sm-4" style="margin-top:15px;"><b style="padding-left: 10px;">Enter password</b></div><div class="col-sm-8"><input type="password" class="inputbox-cheil-mini" name="configs['+count+'].loginPassword" value="" title="Enter login password " placeholder="Enter login password "/>&nbsp;&nbsp;&nbsp;<input type="text" class="inputbox-cheil-mini" name="configs['+count+'].loginPasswordSelector" value="" title="Enter UI selector (ID/CSS/Xpath)" placeholder="Enter UI selector (ID/CSS/Xpath)" /></div></div><br/></div></div></div><div class="col-sm-2"><img style="width:32px;height:32px;margin-left:25px;margin-top:100px;" onclick="javascript:removeRow(' + count + ')" src="${contextPath}/images/remove.png" /></div></div><br/><br/>';
                $(".after-add-input-more").before(html);
                count++;
            });
        });
</script>