<el:dataTable id="jobRequisitionTable" searchFormName="jobRequisitionSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="jobRequisition"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="jobRequisition">

    <el:dataTableAction accessUrl="${createLink(controller: 'jobRequisition', action: 'show')}"
                        functionName="renderInLineShow" actionParams="id" type="function"
                        class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show jobRequisition')}"/>
</el:dataTable>


<br/><br/>

%{--show the job requisition add button if the state is OPEN--}%
<g:if test="${phaseName.toString() == ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.OPEN.toString()}">
    <div class="clearfix form-actions text-center">

        <el:modal buttons="${buttons}" id="jobRequisitionModal"
                  title="${message(code: 'recruitmentCycle.addjobRequisition.label')}" width="80%"
                  buttonClass=" btn btn-sm btn-primary" hideCancel="true" buttonLabel="+ ادراج">

            <el:modalButton class="btn-sm btn-primary" icon="ace-icon fa fa-check"
                            messageCode="default.button.add.label" onClick="addJobRequisition();"
                            id="addButtonToList" name="addButtonToList"/>

            <el:formGroup>
                <form type="POST" id="addJobRequisitionForm" name="addJobRequisitionForm">
                    <el:hiddenField name="recruitmentCycleId" value="${recruitmentCycleId}"/>
                    <g:render template="/jobRequisition/addJobRequisition"/>
                </form>
            </el:formGroup>
        </el:modal>
        <script>
            function addJobRequisition() {
                $.ajax({
                    url: "${createLink(controller: 'recruitmentCycle',action: 'addJobRequisition')}",
                    data: $("#addJobRequisitionForm").serialize(),
                    type: "POST",
                    dataType: "json",
                    success: function (data) {
                        if (data.success) {
                            _dataTables['jobRequisitionTableToChoose'].draw();
                            _dataTables['jobRequisitionTable'].draw();
                            $("#jobRequisitionModal").modal('toggle');
                        }
                        else {

                        }
                    },
                    error: function (xhr, status) {
                    }
                });
            }
        </script>

    </div>
</g:if>
</body>
</html>
