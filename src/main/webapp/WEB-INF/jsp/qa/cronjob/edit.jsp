<%@ include file="../../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <c:if test="${not empty message}">
              <div class="alert alert-danger" role="alert" id="errorMessage">
                  <spring:message code="${message}" />
              </div>
          </c:if>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/qaCronJob/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/qaCronJob')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" type="button" title="Save">Save</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2"  aria-controls="tableData" type="button" id="runBtn" title="Run">Run</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="copyLocator('${editForm.recordId}');" aria-controls="tableData" title="Copy" type="button">Copy</button>
                </div>
            </div>
        </div>
        <%@ include file="../../commonFields.jsp" %>
        <div class="row">
            <form:input path="recordId" class="inputbox-cheil" type="hidden" />
            <div class="col-sm-4">
                     <form:input path="identifier" class="inputbox-cheil" placeholder="Enter Id" required="required" />
                     <span>Enter Id</span>
            </div>
            <div class="col-sm-4">
                 <form:input path="emailSubject" class="inputbox-cheil" placeholder="Enter email subject" required="required" />
                 <span>Enter email subject</span>
            </div>
            <div class="col-sm-4">
                Enable Debug? <form:checkbox style= "width: 40px;height: 20px;" path="isDebug"  id="isDebug"/>
            </div>
        </div>
        <br/><br/>
        <div class="row">
                <div class="col-sm-6">
                   <form:input id="cron-output" path="cronExpression" readonly="true" class="inputbox-cheil cron-output" placeholder="Cron Expression" onkeyup="importCronExpressionFromInput('#cron-output')" />
                   <span>Cron Expression</span>
                 </div>
                 <div class="col-sm-6">
                     <select id="dashboard" name="dashboard" class="cheil-select" required="required">
                         <option value="">Select a dashboard</option>
                         <c:forEach items="${dashboards}" var="child">
                             <c:choose>
                                 <c:when test="${editForm.dashboard == child.id}">
                                   <option value="${child.id}" selected>${child.identifier}</option>
                                 </c:when>
                                 <c:otherwise>
                                    <option value="${child.id}" >${child.identifier}</option>
                                 </c:otherwise>
                             </c:choose>
                         </c:forEach>
                     </select>
                 </div>
        </div>
        <br/><br/>
        <div class="row">
        <div class="col-sm-6">
         <select id="node" name="node" class="cheil-select" required="required">
                   <option value="">Select a node</option>
                   <c:forEach items="${nodes}" var="child">
                   <c:choose>
                    <c:when test="${editForm.node eq child.path}">
                      <option value="${child.path}" selected>${child.name}</option>
                    </c:when>
                    <c:otherwise>
                       <option value="${child.path}" >${child.name}</option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
        </div>
        <div class="col-sm-6">
            <select id="profiles" name="cronProfileId" class="cheil-select">
                <option value="">Select a cron job profile</option>
                <c:forEach items="${profiles}" var="child">
                    <c:choose>
                        <c:when test="${editForm.cronProfileId == child.id}">
                          <option value="${child.id}" selected>${child.identifier}</option>
                        </c:when>
                        <c:otherwise>
                           <option value="${child.id}" >${child.identifier}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </div>
        </div>
         <br/><br/>
        <div class="row">
            <div class="col-sm-3">
                <form:textarea path="skus" id="skus" rows="10" cols="45" class="inputbox-cheil-textarea" placeholder="Please enter SKU's"/>
                <span class="searchtext">Please enter SKU's</span>
                <form:errors path="skus" class="text-danger"></form:errors>
            </div>
             <div class="col-sm-3">
                 <form:textarea path="campaign" id="campaign" rows="10" cols="45" class="inputbox-cheil-textarea" placeholder="Please enter campaign"/>
                 <span class="searchtext">Please enter campaign</span>
                 <form:errors path="campaign" class="text-danger"></form:errors>
        </div>
        <div class="col-sm-3">
            <select id="envProfileId" name="envProfiles" class="cheil-select" multiple="multiple">
                <option value="">Select env profile</option>
                <c:forEach items="${envProfiles}" var="child">
                    <c:choose>
                        <c:when test="${fn:contains( editForm.envProfiles, child ) }">
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
             <select name="shopCampaign" placeholder="Select shop campaign" class="3col active cheil-select" id="pathsPicker">
                 <option value="">Select shop campaign</option>
                 <c:forEach items="${shopCampaigns}" var="child">
                     <c:choose>
                         <c:when test="${editForm.shopCampaign == child.recordId}">
                           <option value="${child.recordId}" selected>${child.identifier}</option>
                         </c:when>
                         <c:otherwise>
                            <option value="${child.recordId}" >${child.identifier}</option>
                         </c:otherwise>
                     </c:choose>
                 </c:forEach>
             </select>
            </div>
        </div>
        <br/><br/>
        <c:forEach items="${editForm.cronTestPlanFormList}" var="cronTestPlan" varStatus="loop">
        <div class="row" id="qaCronJob${loop.index}">
            <div class="col-sm-1">
                <select id="subsidiary-${loop.index}" name="cronTestPlanFormList[${loop.index}].subsidiary" class="cheil-select" required="required">
                    <option value="">Select a subsidiary</option>
                    <c:forEach items="${subsidiaries}" var="child">
                        <c:choose>
                            <c:when test="${cronTestPlan.subsidiary == child.id}">
                              <option value="${child.id}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.id}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-1">
                <select id="siteIsoCode-${loop.index}" name="cronTestPlanFormList[${loop.index}].siteIsoCode" class="cheil-select" required="required">
                    <option value="">Select a site</option>
                    <c:forEach items="${sites}" var="child">
                        <c:choose>
                            <c:when test="${cronTestPlan.siteIsoCode == child.siteId}">
                              <option value="${child.siteId}" selected>${child.siteId}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.siteId}" >${child.siteId}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-1">
                <select class="cheil-select" name="cronTestPlanFormList[${loop.index}].testProfile" id="testProfile-${loop.index}" placeholder="Select test profile" required="required">
                <span>Select test profile</span>
                  <option value="" >Select test profile</option>
                     <c:forEach items="${testProfiles}" var="profile">
                        <c:choose>
                            <c:when test="${cronTestPlan.testProfile == profile.recordId}">
                              <option value="${profile.recordId}" selected>${profile.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${profile.recordId}" >${profile.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                     </c:forEach>
                </select>
            </div>
            <div class="col-sm-2">
                <select class="cheil-select" name="cronTestPlanFormList[${loop.index}].environment" id="environment-${loop.index}" placeholder="Select Environment" required="required">
                <span>Select Environment</span>
                  <option value="" >Select Environment</option>
                     <c:forEach items="${environments}" var="child">
                        <c:choose>
                            <c:when test="${cronTestPlan.environment == child.recordId}">
                              <option value="${child.recordId}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.recordId}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                     </c:forEach>
                </select>
            </div>
             <div class="col-sm-2">
                   <select id="testPlan-${loop.index}" name="cronTestPlanFormList[${loop.index}].testPlan" class="cheil-select" required="required">
                      <option value="">Select a test plan</option>
                      <c:forEach items="${testPlans}" var="child">
                          <c:choose>
                              <c:when test="${cronTestPlan.testPlan == child.recordId}">
                                <option value="${child.recordId}" selected>${child.identifier}</option>
                              </c:when>
                              <c:otherwise>
                                 <option value="${child.recordId}" >${child.identifier}</option>
                              </c:otherwise>
                          </c:choose>
                      </c:forEach>
                  </select>
              </div>
              <div class="col-sm-2">
                <select class="cheil-select" name="cronTestPlanFormList[${loop.index}].categoryId" id="category-${loop.index}" placeholder="Select Category" required="required">
                <span>Select Category</span>
                  <option value="" >Select Category</option>
                     <c:forEach items="${categories}" var="category">
                        <c:choose>
                            <c:when test="${cronTestPlan.categoryId == category.categoryId}">
                              <option value="${category.categoryId}" selected>${category.categoryId}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${category.categoryId}" >${category.categoryId}</option>
                            </c:otherwise>
                        </c:choose>
                     </c:forEach>
                </select>
            </div>
            <div class="col-sm-2">
                <select id="profiles-${loop.index}" name="cronTestPlanFormList[${loop.index}].cronProfileId" class="cheil-select" required="required">
                    <option value="">Select a cron job profile</option>
                    <c:forEach items="${profiles}" var="child">
                        <c:choose>
                            <c:when test="${cronTestPlan.cronProfileId == child.id}">
                              <option value="${child.id}" selected>${child.identifier}</option>
                            </c:when>
                            <c:otherwise>
                               <option value="${child.id}" >${child.identifier}</option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </div>
            <div class="col-sm-1">
                <img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeRow('${loop.index}')" src="${contextPath}/images/remove.png" />
            </div>
        </div>
        <br/><br/>
        </c:forEach>
        <input id="existingTestPlanCount" name="existingTestPlanCount" value="${existingTestPlanCount}" style="display:none;">
        <div class="input-group control-group after-add-input-more">
        <button class="btn btn-primary add-input-more" type="button"><i class="glyphicon glyphicon-add"></i>Add test plan</button>
        <br/><br/>
        <div id="myModalCron" class="modal" tabindex="-1">
               <div class="modal-dialog" style="max-width:900px">
                  <div class="modal-content">
                     <div class="modal-header">
                         Set-up the interval time
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                     </div>
                     <div class="modal-body">
                        <div class="row cron">
                <div class="col-sm-12">
                <form id="cron">
                <div class="col-sm-12 cron-shortcut">
                Common Settings
                                    <select onmousedown="this.value='';" style="height:40px;" onchange="javascript:cronTemplate(this.value);">
                                        <option value='0' selected>Select All</option>
                                        <option value='4'>Every hour</option>
                                        <option value='6'>Every 4 hours (beginning at 0am) (except for Saturday and Sunday)</option>
                                        <option value='8'>Every 12 hours (beginning at 0am) (except for Saturday and Sunday)</option>
                                        <option value='9'>Everyday at 8 am (except for Saturday and Sunday)</option>
                                        <option value='10'>Everyday</option>
                                    </select>
                                </div>
                                </div>
                                <div class="col-sm-12 cron-shortcut">
                			<div class="col-sm-6">
                				<strong>Hours:</strong><br>
                				<select size="12" multiple="multiple" name="cron-hours" class="cron-hours" id="cron-hours" onchange="updateField('hours')">
                					<option selected="selected" value="0">00</option>
                					<option selected="selected" value="1">01</option>
                					<option selected="selected" value="2">02</option>
                					<option selected="selected" value="3">03</option>
                					<option selected="selected" value="4">04</option>
                					<option selected="selected" value="5">05</option>
                					<option selected="selected" value="6">06</option>
                					<option selected="selected" value="7">07</option>
                					<option selected="selected" value="8">08</option>
                					<option selected="selected" value="9">09</option>
                					<option selected="selected" value="10">10</option>
                					<option selected="selected" value="11">11</option>
                					<option selected="selected" value="12">12</option>
                					<option selected="selected" value="13">13</option>
                					<option selected="selected" value="14">14</option>
                					<option selected="selected" value="15">15</option>
                					<option selected="selected" value="16">16</option>
                					<option selected="selected" value="17">17</option>
                					<option selected="selected" value="18">18</option>
                					<option selected="selected" value="19">19</option>
                					<option selected="selected" value="20">20</option>
                					<option selected="selected" value="21">21</option>
                					<option selected="selected" value="22">22</option>
                					<option selected="selected" value="23">23</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-hours')" style="color:blue;">Select All</a>
                			</div>
                			<div class="col-sm-6">
                				<strong>Minutes:</strong><br>
                				<select size="12" multiple="multiple" name="cron-minutes" class="cron-minutes" id="cron-minutes" onchange="updateField('minutes')">
                					<option selected="selected" value="0">00</option>
                					<option selected="selected" value="1">01</option>
                					<option selected="selected" value="2">02</option>
                					<option selected="selected" value="3">03</option>
                					<option selected="selected" value="4">04</option>
                					<option selected="selected" value="5">05</option>
                					<option selected="selected" value="6">06</option>
                					<option selected="selected" value="7">07</option>
                					<option selected="selected" value="8">08</option>
                					<option selected="selected" value="9">09</option>
                					<option selected="selected" value="10">10</option>
                					<option selected="selected" value="11">11</option>
                					<option selected="selected" value="12">12</option>
                					<option selected="selected" value="13">13</option>
                					<option selected="selected" value="14">14</option>
                					<option selected="selected" value="15">15</option>
                					<option selected="selected" value="16">16</option>
                					<option selected="selected" value="17">17</option>
                					<option selected="selected" value="18">18</option>
                					<option selected="selected" value="19">19</option>
                					<option selected="selected" value="20">20</option>
                					<option selected="selected" value="21">21</option>
                					<option selected="selected" value="22">22</option>
                					<option selected="selected" value="23">23</option>
                					<option selected="selected" value="24">24</option>
                					<option selected="selected" value="25">25</option>
                					<option selected="selected" value="26">26</option>
                					<option selected="selected" value="27">27</option>
                					<option selected="selected" value="28">28</option>
                					<option selected="selected" value="29">29</option>
                					<option selected="selected" value="30">30</option>
                					<option selected="selected" value="31">31</option>
                					<option selected="selected" value="32">32</option>
                					<option selected="selected" value="33">33</option>
                					<option selected="selected" value="34">34</option>
                					<option selected="selected" value="35">35</option>
                					<option selected="selected" value="36">36</option>
                					<option selected="selected" value="37">37</option>
                					<option selected="selected" value="38">38</option>
                					<option selected="selected" value="39">39</option>
                					<option selected="selected" value="40">40</option>
                					<option selected="selected" value="41">41</option>
                					<option selected="selected" value="42">42</option>
                					<option selected="selected" value="43">43</option>
                					<option selected="selected" value="44">44</option>
                					<option selected="selected" value="45">45</option>
                					<option selected="selected" value="46">46</option>
                					<option selected="selected" value="47">47</option>
                					<option selected="selected" value="48">48</option>
                					<option selected="selected" value="49">49</option>
                					<option selected="selected" value="50">50</option>
                					<option selected="selected" value="51">51</option>
                					<option selected="selected" value="52">52</option>
                					<option selected="selected" value="53">53</option>
                					<option selected="selected" value="54">54</option>
                					<option selected="selected" value="55">55</option>
                					<option selected="selected" value="56">56</option>
                					<option selected="selected" value="57">57</option>
                					<option selected="selected" value="58">58</option>
                					<option selected="selected" value="59">59</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-minutes')" style="color:blue;">Select All</a>
                			</div>
                			</div>
                			<div class="col-sm-12 cron-shortcut">
                			<div class="col-sm-6">
                				<strong>Day of Month:</strong><br>
                				<select size="12" multiple="multiple" name="cron-dom" id="cron-dom" class="cron-dom" onchange="updateField('dom')">
                					<option selected="selected" value="1">01</option>
                					<option selected="selected" value="2">02</option>
                					<option selected="selected" value="3">03</option>
                					<option selected="selected" value="4">04</option>
                					<option selected="selected" value="5">05</option>
                					<option selected="selected" value="6">06</option>
                					<option selected="selected" value="7">07</option>
                					<option selected="selected" value="8">08</option>
                					<option selected="selected" value="9">09</option>
                					<option selected="selected" value="10">10</option>
                					<option selected="selected" value="11">11</option>
                					<option selected="selected" value="12">12</option>
                					<option selected="selected" value="13">13</option>
                					<option selected="selected" value="14">14</option>
                					<option selected="selected" value="15">15</option>
                					<option selected="selected" value="16">16</option>
                					<option selected="selected" value="17">17</option>
                					<option selected="selected" value="18">18</option>
                					<option selected="selected" value="19">19</option>
                					<option selected="selected" value="20">20</option>
                					<option selected="selected" value="21">21</option>
                					<option selected="selected" value="22">22</option>
                					<option selected="selected" value="23">23</option>
                					<option selected="selected" value="24">24</option>
                					<option selected="selected" value="25">25</option>
                					<option selected="selected" value="26">26</option>
                					<option selected="selected" value="27">27</option>
                					<option selected="selected" value="28">28</option>
                					<option selected="selected" value="29">29</option>
                					<option selected="selected" value="30">30</option>
                					<option selected="selected" value="31">31</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-dom')" style="color:blue;">Select All</a>
                			</div>
                			<div class="col-sm-6">
                				<strong>Month:</strong><br>
                				<select size="12" multiple="multiple" name="cron-months" id="cron-months" class="cron-months" onchange="updateField('months')">
                					<option selected="selected" value="1">January</option>
                					<option selected="selected" value="2">February</option>
                					<option selected="selected" value="3">March</option>
                					<option selected="selected" value="4">April</option>
                					<option selected="selected" value="5">May</option>
                					<option selected="selected" value="6">June</option>
                					<option selected="selected" value="7">July</option>
                					<option selected="selected" value="8">August</option>
                					<option selected="selected" value="9">September</option>
                					<option selected="selected" value="10">October</option>
                					<option selected="selected" value="11">November</option>
                					<option selected="selected" value="12">December</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-months')" style="color:blue;">Select All</a>
                			</div>
                			</div>
                			<div class="col-sm-12 cron-shortcut">
                			<div class="col-sm-6">
                				<strong>Day of Week:</strong><br>
                				<select size="12" multiple="multiple" name="cron-dow" id="cron-dow" class="cron-dow" onchange="updateField('dow')">
                					<option selected="selected" value="0">Sunday</option>
                					<option selected="selected" value="1">Monday</option>
                					<option selected="selected" value="2">Tuesday</option>
                					<option selected="selected" value="3">Wednesday</option>
                					<option selected="selected" value="4">Thursday</option>
                					<option selected="selected" value="5">Friday</option>
                					<option selected="selected" value="6">Saturday</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-dow')" style="color:blue;">Select All</a>
                			</div>
                			</div>
                			<span style="display:block;clear:both">&nbsp;</span>
                			<div class="col-sm-12" style="text-align:center;">
                			    <button id="closePopupSave" class="btn btn-primary btn-icon" type="button" title="Save">Save</button>
                			</div>
                		</form>
</div>
              </div>
           </div>
                </div>

                </div>
            </div>
       </form:form>
       </div>

    </div>
  </div>
</div>

<script type="text/javascript">
var envProfiles = new Choices('#envProfileId', {
                   removeItemButton: true,
                   maxItemCount:-1,
                   searchResultLimit:20,
                   renderChoiceLimit:-1
                 });
var profilesMain = new Choices('#profiles', {
     removeItemButton: true,
     maxItemCount:-1,
     searchResultLimit:20,
     renderChoiceLimit:-1
});
    var count = $('#existingTestPlanCount').val();
            var subsidiary="";
            var testPlan="";
            var siteIsoCode="";
            var environment="";
            var testProfile="";
            var category="";
            var profiles="";
            for(i=0;i<count;i++){
               subsidiary = new Choices('#subsidiary-'+i, {
                    removeItemButton: true,
                    maxItemCount:-1,
                    searchResultLimit:20,
                    renderChoiceLimit:-1
               });
               testPlan = new Choices('#testPlan-'+i, {
                   removeItemButton: true,
                   maxItemCount:-1,
                   searchResultLimit:20,
                   renderChoiceLimit:-1
              });
              siteIsoCode = new Choices('#siteIsoCode-'+i, {
                  removeItemButton: true,
                  maxItemCount:-1,
                  searchResultLimit:20,
                  renderChoiceLimit:-1
             });
             environment = new Choices('#environment-'+i, {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
            });
            testProfile = new Choices('#testProfile-'+i, {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
            });
            category = new Choices('#category-'+i, {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
            });
            profiles = new Choices('#profiles-'+i, {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
            });
           }
    $(document).ready(function() {
       $(".add-input-more").click(function(){
       var html = '<div class="row" id="qaCronJob'+count+'"><div class="col-sm-1"><select id="subsidiary-'+count+'" name="cronTestPlanFormList[' + count + '].subsidiary" class="cheil-select" required="required"><option value="">Select a subsidiary</option><c:forEach items="${subsidiaries}" var="child"><option value="${child.id}" >${child.identifier}</option></c:forEach></select></div><div class="col-sm-1"><select id="siteIsoCode-'+count+'" name="cronTestPlanFormList[' + count + '].siteIsoCode" class="cheil-select" required="required"><option value="">Select a site</option><c:forEach items="${sites}" var="child"><option value="${child.siteId}" >${child.siteId}</option></c:forEach></select></div><div class="col-sm-1"><select class="cheil-select" name="cronTestPlanFormList[' + count + '].testProfile" id="testProfile-'+count+'"  id="cronTestPlanFormList[' + count + '].testProfile" placeholder="Select test profile" required="required"><span>Select test profile</span><option value="" >Select test profile</option><c:forEach items="${testProfiles}" var="profile"><option value="${profile.recordId}" >${profile.identifier}</option></c:forEach></select></div><div class="col-sm-2"><select class="cheil-select" name="cronTestPlanFormList[' + count + '].environment" id="environment-'+count+'" placeholder="Select Environment" required="required"><span>Select Environment</span><option value="" >Select Environment</option><c:forEach items="${environments}" var="child"><option value="${child.recordId}" >${child.identifier}</option></c:forEach></select></div><div class="col-sm-2"><select id="testPlan-'+count+'" name="cronTestPlanFormList[' + count + '].testPlan" class="cheil-select" required="required"><option value="">Select a test plan</option><c:forEach items="${testPlans}" var="child"><option value="${child.recordId}" >${child.identifier}</option></c:forEach></select></div><div class="col-sm-2"><select class="cheil-select" name="cronTestPlanFormList[' + count + '].categoryId" id="category-'+count+'" placeholder="Select Category" required="required"><span>Select Category</span><option value="" >Select Category</option><c:forEach items="${categories}" var="category"><option value="${category.categoryId}" >${category.categoryId}</option></c:forEach></select></div><div class="col-sm-2"><select id="profiles-'+count+'" name="cronTestPlanFormList['+count+'].cronProfileId" class="cheil-select" required="required"><option value="">Select a cron job profile</option><c:forEach items="${profiles}" var="child"><option value="${child.id}" >${child.identifier}</option></c:forEach></select></div><div class="col-sm-1"><img style="width:32px;height:32px;margin-left:25px;margin-bottom:10px;" onclick="javascript:removeRow('+count+')" src="${contextPath}/images/remove.png" /></div></div><br/><br/>'
        $(".after-add-input-more").before(html);
           subsidiary = new Choices('#subsidiary-'+count, {
                removeItemButton: true,
                maxItemCount:-1,
                searchResultLimit:20,
                renderChoiceLimit:-1
           });
           siteIsoCode = new Choices('#siteIsoCode-'+count, {
                 removeItemButton: true,
                 maxItemCount:-1,
                 searchResultLimit:20,
                 renderChoiceLimit:-1
           });
           testProfile = new Choices('#testProfile-'+count, {
                removeItemButton: true,
                maxItemCount:-1,
                searchResultLimit:20,
                renderChoiceLimit:-1
          });
          category = new Choices('#category-'+count, {
              removeItemButton: true,
              maxItemCount:-1,
              searchResultLimit:20,
              renderChoiceLimit:-1
        });
          environment = new Choices('#environment-'+count, {
              removeItemButton: true,
              maxItemCount:-1,
              searchResultLimit:20,
              renderChoiceLimit:-1
        });
          testPlan = new Choices('#testPlan-'+count, {
              removeItemButton: true,
              maxItemCount:-1,
              searchResultLimit:20,
              renderChoiceLimit:-1
        });
        profiles = new Choices('#profiles-'+count, {
                         removeItemButton: true,
                         maxItemCount:-1,
                         searchResultLimit:20,
                         renderChoiceLimit:-1
                    });
           count++;
       });

       $("#cron-output").click(function(){
             $("#myModalCron").modal('show');
            });
            $("#closePopupSave").click(function(){
             $("#myModalCron").modal('hide');
            });
      });

      $('[id*="subsidiary-"]').change(function(){
             var subsidiaryId = $(this).val();
             if(subsidiaryId!=''){
                 $.ajax({
                     type: 'GET',
                     url: "/toolkit/import/getSitesForSubsidiary/" + subsidiaryId,
                     datatype: "json",
                     success: function(data){
                     var jsonData = [];
                    for(var i=0; i<data.length; i++){
                         jsonData.push({
                            "value" : data[i].siteId,
                            "label" : data[i].siteId
                        });
                    }
                    console.log(jsonData);
                    siteIsoCode.setChoices(jsonData,'value','label', true);

                     },
                     error:function(e){
                         console.log(e.statusText);
                     }
                 });
             }
         });

       $('[id*="subsidiary-"]').change(function(){
           var subsidiaryId = $(this).val();
           if(subsidiaryId!=''){
               $.ajax({
                   type: 'GET',
                   url: "/qa/getTestPlanForSubsidiary/" + subsidiaryId,
                   datatype: "json",
                   success: function(data){
                   $('#submitButton').show();
                   var jsonData = [];
                     for(var i=0; i<data.length; i++){
                          jsonData.push({
                             "value" : data[i].recordId,
                             "label" : data[i].identifier
                         });
                     }
                 testPlan.setChoices(jsonData,'value','label', true);
                   },
                   error:function(e){
                       console.log(e.statusText);
                   }
               });
           }
       });

       $('[id*="subsidiary-"]').change(function(){
            var subsidiaryId = $(this).val();
            if(subsidiaryId!=''){
                $.ajax({
                    type: 'GET',
                    url: "/qa/getEnvironmentsForSubsidiary/" + subsidiaryId,
                    datatype: "json",
                    success: function(data){
                    var jsonData = [];
                       for(var i=0; i<data.length; i++){
                            jsonData.push({
                               "value" : data[i].recordId,
                               "label" : data[i].identifier
                           });
                       }
                   environment.setChoices(jsonData,'value','label', true);
                    },
                    error:function(e){
                        console.log(e.statusText);
                    }
                });
            }
       });

       $('[id*="subsidiary-"]').change(function(){
            var subsidiaryId = $(this).val();
            if(subsidiaryId!=''){
                $.ajax({
                    type: 'GET',
                    url: "/qa/getTestProfileForSubsidiary/" + subsidiaryId,
                    datatype: "json",
                    success: function(data){
                    var jsonData = [];
                           for(var i=0; i<data.length; i++){
                                jsonData.push({
                                   "value" : data[i].recordId,
                                   "label" : data[i].identifier
                               });
                           }
                       testProfile.setChoices(jsonData,'value','label', true);
                    },
                    error:function(e){
                        console.log(e.statusText);
                    }
                });
            }
        });
        $('[id*="subsidiary-"]').change(function(){
            var subsidiaryId = $(this).val();
            if(subsidiaryId!=''){
                $.ajax({
                    type: 'GET',
                    url: "/qa/getCategoryForSubsidiary/" + subsidiaryId,
                    datatype: "json",
                    success: function(data){
                    var jsonData = [];
                           for(var i=0; i<data.length; i++){
                                jsonData.push({
                                   "value" : data[i].id,
                                   "label" : data[i].categoryId
                               });
                           }
                       category.setChoices(jsonData,'value','label', true);
                    },
                    error:function(e){
                        console.log(e.statusText);
                    }
                });
            }
        });
        function removeRow(countVal) {
           $('#qaCronJob'+countVal).remove();
        }
         $("#runBtn").click(function(){
              var form = $('#editForm');
                   $.ajax({
                       type: 'POST',
                       data: form.serialize(),
                       url: "/admin/qaCronJob/run",
                       datatype: "json",
                       success: function(data){
                       if(data == 'Success'){
                        fire_ajax_submit('${contextPath}/admin/qaCronJob');
                       }
                       },
                       error:function(e){
                           console.log(e.statusText);
                       }
                   });
               });

            function copyLocator(groupId){
                event.stopImmediatePropagation();
                event.preventDefault();
                $.ajax({
                  type: 'GET',
                  url: "/admin/qaCronJob/copy/" + groupId,
                  datatype: "json",
                  success: function(data) {
                    fire_ajax_submit('/admin/qaCronJob');
                  },
                  error:function(e){
                      console.log(e.statusText);
                  }
              });
            }
            $(document).ready(function() {
                 $('[id*="selectpicker"]').multiselect({
                            enableClickableOptGroups: true,
                            enableCollapsibleOptGroups: true,
                            collapseOptGroupsByDefault:true,
                            enableFiltering: true,
                            includeSelectAllOption: true,
                            buttonWidth: '100%',
                            includeResetOption: true,
                            includeResetDivider: true,
                            enableFiltering: true,
                            resetText: "Reset all",
                            dropUp: true,
                            maxHeight: 300,
                            enableHTML: true,
                            buttonText: function(options, select) {
                                    var $select = $(select);
                                    var $optgroups = $('optgroup', $select);
                                            if (options.length === 0) {
                                                return 'Select ' + $optgroups[0].label + ' permissions';
                                            }
                                             else {
                                                var text='';
                                                 $optgroups.each(function() {
                                                      var $selectedOptions = $('option:selected', this);
                                                      var $options = $('option', this);
                                                      if ($selectedOptions.length == $options.length) {
                                                          text += '<span class="boot-choices">' + $(this).attr('label') + '|' + 'All Nodes ' + '</span>';
                                                      }
                                                      else if($selectedOptions.length > 0) {
                                                          text += '<span class="boot-choices">' + $(this).attr('label') + '|';
                                                          $selectedOptions.each(function() {
                                                              text += $(this).text() + ',';
                                                          });
                                                          text = text.substr(0, text.length - 1);
                                                          text += '</span>';
                                                      }
                                                  });
                                                  return text;
                                             }
                                        }
                        });

            });


 </script>