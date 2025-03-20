<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/domTree/mangeShop" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <a href="/admin/domTree/exportDomTree" style="padding-top:7px" target="_blank" class="btn btn-primary btn-icon btn-icon-small">Export dom</a>
                    <a href="/admin/domTree/exportDuplicateRecords" style="padding-top:7px" target="_blank" class="btn btn-primary btn-icon btn-icon-small">Export duplicate</a>
                    <a href="/admin/domTree/repairRecords" style="padding-top:7px" target="_blank" class="btn btn-primary btn-icon btn-icon-small">Repair</a>
                </div>
            </div>
        </div>
        <br/><br/>
        <div class="row">
            <div class="col-sm-8">
                 <textarea id="identifier" name="identifier" rows="10" cols="53" class="inputbox-cheil-textarea" placeholder="please enter error types"></textarea>
                 <span class="searchtext">Please enter error types</span>
                 <form:errors path="identifier" class="text-danger"></form:errors>
             </div>
             <div class="col-sm-4">
                <button class="btn btn-primary btn-icon btn-icon-small" title="Submit" onclick="ajaxformSubmit('editForm');" tabindex="0" aria-controls="tableData" type="submit">Submit</button>
             </div>
        </div>
        <br/><br/>

       </form:form>
       <c:if test="${not empty message}">
           <div class="alert alert-danger" role="alert" id="errorMessage">
               <spring:message code="${message}" />
           </div>
       </c:if>
</div>

