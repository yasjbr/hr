
<g:render template="/employee/wrapper" model="[id          : (employeeCallBackId ?: 'employeeId'),
                                               name        : 'employee.id',
                                               isHiddenInfo: params.isHiddenPersonInfo,
                                               bean        : trainingRecord?.employee,
                                               isDisabled  : isEmployeeDisabled]"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="trainingClassification" paramsGenerateFunction="trainingClassificationParam"
                     action="autocomplete" id="trainingClassificationId" name="trainingClassification.id"
                     label="${message(code: 'trainingRecord.trainingClassification.label',
                             default: 'trainingClassification')}"
                     values="${[[trainingRecord?.trainingClassification?.id,
                                 trainingRecord?.trainingClassification?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:dualAutocomplete class="isRequired"
                         label="${message(code: 'trainingRecord.trainingCourse.label', default: 'trainingCourse')}"
                         name="trainingCourse.id" action="autocomplete"
                         paramsGenerateFunction="sendTrainingCourseParams"
                         controller="trainingCourse" size="8"
                         values="${[[trainingRecord?.trainingCourse?.id,
                                     trainingRecord?.trainingCourse?.descriptionInfo?.localName]]}"
                         textName="trainingName"
                         textValue="${trainingRecord?.trainingName}"/>
</el:formGroup>


<el:formGroup>
    <el:dateField name="fromDate" size="8" class=" "
                  label="${message(code: 'trainingRecord.fromDate.label', default: 'fromDate')}"
                  value="${trainingRecord?.fromDate}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate" size="8" class=" "
                  label="${message(code: 'trainingRecord.toDate.label', default: 'toDate')}"
                  value="${trainingRecord?.toDate}"/>
</el:formGroup>

<el:formGroup>
    <el:dualAutocomplete class="isRequired"
                         label="${message(code: 'trainingRecord.organizationId.label', default: 'organizationId')}"
                         name="organizationId" action="autocompl ete"
                         controller="organization" size="8"
                         values="${[[trainingRecord?.organizationId,
                                     trainingRecord?.transientData?.organizationDTO?.descriptionInfo?.localName]]}"
                         textName="organizationName"
                         textValue="${trainingRecord?.organizationName}"/>
</el:formGroup>

<el:formGroup>
    <el:dualAutocomplete class=" " label="${message(code: 'trainingRecord.trainer.label', default: 'trainer')}"
                         name="trainer.id" action="autocomplete"
                         controller="trainer" size="8"
                         values="${[[trainingRecord?.trainer?.id, trainingRecord?.trainer?.transientData?.personDTO?.localFullName]]}"
                         textName="trainerName"
                         textValue="${trainingRecord?.trainerName}"/>
</el:formGroup>



<el:formGroup>
    <el:textField name="certificate" size="8"
                  label="${message(code: 'trainingRecord.certificate.label',
                          default: 'certificate')}"
                  value="${trainingRecord?.certificate}"/>
</el:formGroup>

<el:formGroup>
    <el:integerField name="numberOfTrainee" size="8" class=" isNumber"
                     label="${message(code: 'trainingRecord.numberOfTrainee.label',
                             default: 'numberOfTrainee')}"
                     value="${trainingRecord?.numberOfTrainee}"/>
</el:formGroup>

<el:formGroup>
    <div class="col-xs-12 col-sm-12 col-md-8 pcp-form-control">

        <label class="col-xs-4 col-sm-4 col-md-4 control-label no-padding-right text-left">
            ${message(code: 'trainingRecord.period.label', default: 'period')}
        </label>


        <div class="col-xs-4 col-sm-4 col-md-4">

            <input type="text" class="form-control input-integer"
                   data-placement="bottom" id="period" name="period" value="${trainingRecord?.period}"/>

        </div>

        <div class="col-xs-4 col-sm-4 col-md-4">

            <g:select icon="icon-search" customOptionKey="id" customOptionValue="name"
                      ajaxUrl="${createLink(controller: 'unitOfMeasurement', action: 'autocomplete')}"
                      class="select2-remote form-control" from="${[]}"
                      name="unitId" isMultiple="false" preventSpaces="false"
                      paramsGenerateFunction="unitOfMeasurementParam"
                      values="${[[id: trainingRecord?.unitId ?: "", text: trainingRecord?.transientData?.unitDTO?.descriptionInfo?.toString() ?: ""] as grails.converters.JSON]}"/>
        </div>

    </div>
</el:formGroup>

<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'trainingRecord.note.label', default: 'note')}"
                 value="${trainingRecord?.note}"/>
</el:formGroup>

<lay:wall title="${g.message(code: 'trainingRecord.locationId.label', default: 'location')}">
    <g:render template="/pcore/location/staticWrapper" model="[
            isCountryRequired: true,
            hiddenDetails    : true,
            size             : 8,
            location         : trainingRecord?.transientData?.locationDTO
    ]"/>
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8" class=" "
                     label="${message(code: 'location.unstructuredLocation.label', default: 'unstructuredLocation')}"
                     value="${trainingRecord?.unstructuredLocation}"/>
    </el:formGroup>
</lay:wall>




<script type="text/javascript">
    function sendTrainingCourseParams() {
        return {'trainingClassification.id': $('#trainingClassificationId').val()}
    }

    function unitOfMeasurementParam() {
        return {'unitCategory.id': '${ps.police.pcore.enums.v1.UnitCategory.TIME.value()}'}
    }


    function trainingClassificationParam() {
        return {'firm.id': '${firmId}'}
    }

</script>