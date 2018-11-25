<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'vacancy.entity', default: 'Vacancy List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'Vacancy List')}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'vacancy', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <div style="background:gainsboro;">
                <el:formGroup>
                    <el:modal buttons="${buttons}" id="modal-form" title="" width="70%"
                              buttonClass=" btn btn-sm btn-primary" hideCancel="true"
                              buttonLabel="${message(code: 'vacancy.copyFromJobRequisition.label')}">
                        <lay:collapseWidget id="jobRequisitionCollapseWidget" icon="icon-search"
                                            title="${message(code: 'vacancy.jobRequisitionSearch.label')}"
                                            size="12" collapsed="true" data-toggle="collapse">
                            <lay:widgetBody>
                                <el:form action="#" name="jobRequisitionSearchFormForVacancy">

                                    <g:render template="/jobRequisition/searchForVacancy" model="[:]"/>
                                    <el:formButton functionName="search"
                                                   onClick="_dataTables['jobRequisitionTable1'].draw()"/>
                                    <el:formButton functionName="clear"
                                                   onClick="gui.formValidatable.resetForm('jobRequisitionSearchFormForVacancy');_dataTables['jobRequisitionTable1'].draw();"/>
                                </el:form>
                            </lay:widgetBody>
                        </lay:collapseWidget>

                        <el:formGroup>
                            <form id="jobRequsitionDataTableForm">
                                <g:render template="/jobRequisition/dataTableToShowInVacancy"/>

                            </form>
                        </el:formGroup>
                        <el:modalButton class="btn-sm btn-primary" icon="ace-icon fa fa-check"
                                        messageCode="default.button.create.label"
                                        onClick="getJobRequisitionInfo();"/>
                    </el:modal>
                </el:formGroup>
            </div>


            <form style="display: none;" id="jobRequisitionWithSameJobTitleName">
                <el:hiddenField type="enum" name="requisitionStatus" value="APPROVED"/>
                <el:hiddenField name="jobRequisitionId" id="jobRequisitionId" value=""/>
                <el:hiddenField name="forShow" value="true"/>
            </form>



            <el:validatableResetForm name="vacancyForm" callBackFunction="successCallBack"
                                     controller="vacancy" action="save">
                <g:render template="/vacancy/form" model="[vacancy: vacancy]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>

</body>
</html>
