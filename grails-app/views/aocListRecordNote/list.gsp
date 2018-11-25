<g:set var="entities" value="${message(code: 'promotionListEmployeeNote.entities', default: 'promotionListEmployeeNote List')}"/>
<g:set var="entity" value="${message(code: 'promotionListEmployeeNote.entity', default: 'promotionListEmployeeNote List')}"/>
<g:set var="dataTableTitle" value="${message(code: 'default.list.label', args: [entities], default: 'promotionListEmployeeNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>

    <lay:widget id="promotionListEmployeeNoteCollapseWidget"
                        size="12" collapsed="true">
        <lay:widgetToolBar>
            <btn:buttonGroup>
                <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                              link="${createLink(controller: 'aocListRecordNote', action: 'createModal', params: ['listRecord.id':listRecordId])}"
                              label="${message(code: 'default.button.create.label')}">
                    <i class="icon-plus"></i>
                </el:modalLink>
            </btn:buttonGroup>
        </lay:widgetToolBar>
    </lay:widget>
    <g:form action="#" name="listRecordNoteSearchForm">
        <el:hiddenField name="listRecord.id" value="${listRecordId}"/>
    </g:form>
    <el:dataTable id="listRecordNoteTable" searchFormName="listRecordNoteSearchForm" dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true" widthClass="col-sm-12" controller="aocListRecordNote"
                  spaceBefore="true" hasRow="true" action="filter" serviceName="aocListRecordNote">
        <el:dataTableAction controller="aocListRecordNote" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete" showFunction="manageDeleteAction"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete Note')}"/>
    </el:dataTable>
</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
    function manageDeleteAction(row) {
        if(row.recordStatus==null || row.recordStatus==""){
            return true;
        }
        return false;
    }
</script>