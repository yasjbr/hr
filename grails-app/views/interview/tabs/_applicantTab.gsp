<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'interview.entity', default: 'applicant List')}"/>
    <g:set var="tabEntity" value="${message(code: 'applicant.entity', default: 'applicant ')}"/>
    <g:set var="tabEntities" value="${message(code: 'interview.applicant.label', default: 'applicant ')}"/>
    <g:set var="tabList"
           value="${message(code: 'default.list.label', args: [tabEntities], default: 'list applicant ')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create applicant ')}"/>


    %{--this form to get applicant that is assign to  interview --}%
    <el:form action="#" style="display: none;" name="applicantForm">
        <el:hiddenField name="interview.id" id="interviewId" value="${entityId}"/>
        <el:hiddenField name="withRemoting" value="true"/>
    </el:form>


    %{--this form to get applicant that has no interview--}%
    <el:form action="#" style="display: none;" name="applicantForm1">
        <el:hiddenField name="withRemotingValues" value="true"/>
        <el:hiddenField name="hasNoInterview" id="hasNoInterview" value="true"/>

    </el:form>

    <g:render template="/applicant/dataTable"/>
</div>

%{--to show/hide modal using interview status--}%
<g:if test="${params.interviewStatus == 'OPEN'}">
    <div class="clearfix form-actions text-center">

        <el:modalLink
                link="${createLink(controller: 'interview', action: 'getApplicants', id:entityId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="+ ادراج" >
        </el:modalLink>

        %{--<el:modal buttons="${buttons}" id="modal-form1"
                  title="${message(code: 'interview.addApplicantToInterview.label')}"
                  width="70%"
                  buttonClass=" btn btn-sm btn-primary" hideCancel="true" buttonLabel="+ ادراج">
            <el:modalButton class="btn-sm btn-primary" icon="ace-icon fa fa-check"
                            messageCode="default.button.add.label" onClick="addApplicantToInterview();"
                            id="addButtonToList" name="addButtonToList"/>

               <lay:collapseWidget id="applicantCollapseWidget" icon="icon-search"
                                   title="${message(code: 'interview.applicantSearch.label')}"
                                   size="12" collapsed="true" data-toggle="collapse">
                   <lay:widgetBody>
                       <el:form action="#" id="applicantSearchFormModal" name="applicantSearchFormModal">
                           <g:render template="/applicant/searchForInterview" model="[:]"/>
                           <el:formButton functionName="search"
                                          onClick="_dataTables['applicantTable1'].draw()"/>
                           <el:formButton functionName="clear"
                                          onClick="gui.formValidatable.resetForm('applicantSearchFormModal');_dataTables['applicantTable1'].draw();"/>
                       </el:form>
                   </lay:widgetBody>
               </lay:collapseWidget>

            <el:formGroup>
                <form id="applicantFormModal" style="text-align: right;">
                    <el:hiddenField name="interviewId" value="${entityId}"/>
                    <g:render template="/interview/addApplicant"/>
                </form>
            </el:formGroup>

        </el:modal>--}%
    </div>

</g:if>




<script>
    function addApplicantToInterview(id) {
        $('.alert').html("");
        $.ajax({
            url: '${createLink(controller: 'interview',action: 'addApplicantToInterview')}',
            type: 'POST',
            data: $("#applicantFormModal").serialize(),
            dataType: 'json',
            success: function (data) {
                if (data.success) {
                    _dataTables['applicantTable'].draw();
                    _dataTables['applicantTable1'].draw();
                    $('#application-modal-main-content').modal('hide');
                }
            }
        });
    }
</script>

