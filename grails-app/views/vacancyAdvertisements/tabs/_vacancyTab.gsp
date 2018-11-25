<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'vacancyAdvertisements.entity', default: 'vacancy List')}"/>
    <g:set var="tabEntity" value="${message(code: 'vacancy.entity', default: 'vacancy ')}"/>
    <g:set var="tabEntities" value="${message(code: 'vacancy.entities', default: 'vacancy ')}"/>
    <g:set var="tabList" value="${message(code: 'default.list.label', args: [tabEntities], default: 'list vacancy ')}"/>
    <g:set var="tabTitle"
           value="${message(code: 'default.create.label', args: [tabEntity], default: 'create vacancy ')}"/>

    <el:form action="#" style="display: none;" name="vacancyForm">
        <el:hiddenField name="filterByStatus" value="true"/>
        <el:hiddenField name="vacancyAdvertisements.id" value="${entityId}"/>
    </el:form>


    <g:render template="/vacancy/dataTable"
              model="[isInLineActions: true, title: tabList, entity: entity, domainColumns: 'DOMAIN_TAB_COLUMNS']"/>


</div>

<div class="clearfix form-actions text-center">

    <el:modal buttons="${buttons}" id="modal-form1" title="${message(code: 'vacancyAdvertisements.addVacancy.label')}"
              width="70%"
              buttonClass=" btn btn-sm btn-primary" hideCancel="true" buttonLabel="+ ادراج">
        <el:modalButton class="btn-sm btn-primary" icon="ace-icon fa fa-check"
                        messageCode="default.button.add.label" onClick="addVacancyToVacancyAdvertisements();"
                        id="addButtonToList" name="addButtonToList"/>
        <el:formGroup>
            <form type="POST" id="vacancyFormModal" name="vacancyFormModal">
                <el:hiddenField name="vacancyAdvertisementsId" value="${entityId}"/>
                <g:render template="/vacancyAdvertisements/addVacancy"/>
            </form>
        </el:formGroup>
    </el:modal>
</div>


<script>
    //to add vacancy to advertisement
    function addVacancyToVacancyAdvertisements() {
        $.ajax({
            url: "${createLink(controller: 'vacancyAdvertisements',action: 'addVacancyToVacancyAdvertisements')}",
            data: $("#vacancyFormModal").serialize(),
            type: "POST",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    _dataTables['vacancyTable'].draw();
                    _dataTables['vacancyAdvertisementTable'].draw();
                    $('#modal-form1').modal('hide');
                }
            },
            error: function (xhr, status) {
            }
        });
    }


    //to delete vacancy from advertisements
    function deleteVacancy(id){
        $.ajax({
            url: '${createLink(controller: 'vacancyAdvertisements',action: 'deleteVacancyFromVacancyAdvertisements')}',
            type: 'POST',
            data: {
               encodedId:id
            },
            dataType: 'json',
            success: function(data) {
                _dataTables['vacancyTable'].draw();
                _dataTables['vacancyAdvertisementTable'].draw();
                if(data.success){
                    _dataTables['vacancyTable'].draw();
                    _dataTables['vacancyAdvertisementTable'].draw();
                    gui.formValidatable.showSuccessMessage(data.message);
                }else{
                    gui.formValidatable.showErrorMessage(data.message);
                }
            }
        });
    }









    $(document).ready(function () {
        $('#modal-form1').on('shown.bs.modal', function () {
            gui.initAllForModal.init($('#modal-form1'));
        });

    });
</script>