<%@ include file="../../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-5">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
        <div class="col-sm-7"></div>
    </div>
    <div class="row">
        <div class="col-sm-5">
            <div class="dt-buttons">
                <button class="btn btn-primary btn-icon btn-icon-small" title="Shortcuts" id="import" tabindex="0" aria-controls="tableData" type="button">Shortcuts</button>
                <button class="btn btn-primary btn-icon btn-icon-small" title="Import SKUs" id="modalBtn" tabindex="0" aria-controls="tableData" type="button">Import</button>
                <button class="btn btn-primary btn-icon btn-icon-small" title="Refresh" tabindex="0" aria-controls="tableData" type="button">Refresh</button>
                <button class="btn btn-primary btn-icon btn-icon-small" title="Save" tabindex="0" aria-controls="tableData" type="button">Save</button>
            </div>
       </div>
       <div class="col-sm-7"></div>
    </div>

        <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/toolkit/models/process" class="handle-upload" modelAttribute="editForm" >
            <div class="row">
                <div class="col-sm-1"></div>
                <div class="col-sm-4">
                    <select class="cheil-select" name="models[]" class="expanded" placeholder="Select Models" multiple id="">
                    <span>Select Models</span>
                         <c:forEach items="${models}" var="child">
                             <option value="${child.id}" >${child.modelId}</option>
                         </c:forEach>
                     </select>
                </div>
                <div class="col-sm-7" style="background:lightgrey;"></div>
            </div>
            <div class="row">

                <div class="col-sm-4">
                     <select class="cheil-select" name="sites[]" placeholder="Select Sites" multiple id="selectpicker">
                     <span>Select Sites</span>
                         <c:forEach items="${sites}" var="child">
                            <option value="${child.id}" >${child.siteId}</option>
                         </c:forEach>
                     </select>
                </div>
                <div class="col-sm-1">
                    <button class="btn btn-primary add-more"  aria-controls="tableData"  type="submit"><i class="glyphicon glyphicon-add"></i>Submit</button>
                </div>
                <div class="col-sm-7" style="background:lightgrey;"></div>
            </div>
        </form:form>
        <script type="text/javascript">
                $(document).ready(function() {
                    var multipleCancelButton = new Choices('#selectpicker', {
                            removeItemButton: true,
                            maxItemCount:-1,
                            searchResultLimit:20,
                            renderChoiceLimit:-1
                          });
                });
        </script>
</div>
<%@ include file="../../tableActions.jsp" %>

<script>
    $(document).ready(function () {
    $('.content-wrapper').addClass('toolkit');
    });
</script>