<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%" id="modalFormForJobRequisition">
    <msg:page/>
    <lay:collapseWidget id="vacancyCollapseWidget" icon="icon-search"
                        title="${message(code: 'vacancy.jobRequisitionSearch.label')}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="jobRequisitionWithSameJobTitleName">
                <g:render template="/jobRequisition/searchForVacancyForTheSameName" model="[:]"/>
                <el:formButton functionName="search"
                               onClick="_dataTables['jobRequisitionTableForNumberOfPosition'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('jobRequisitionWithSameJobTitleName');_dataTables['jobRequisitionTableForNumberOfPosition'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>
    <g:render template="/jobRequisition/dataTableToShowInVacancyWithSameJobName"/>
</el:modal>