<g:set var="entities"
       value="${message(code: 'applicant.entities', default: 'applicant List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'applicant List')}"/>


<el:modal isModalWithDiv="true" title="${message(code: 'interview.addApplicantToInterview.label')}"
          width="90%" name="modal-form1"
          id="modal-form1" hideCancel="true">

    <msg:modal/>
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
            <el:hiddenField name="interviewId" value="${interview.id}"/>
            <g:render template="/interview/addApplicant"/>
        </form>
    </el:formGroup>

    <el:modalButton class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                    icon="ace-icon fa fa-floppy-o"
                    onClick="addApplicantToInterview()"
                    id="addButtonToList"
                    message="${g.message(code: 'applicant.button.select.vacancy.label')}"/>

</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>
