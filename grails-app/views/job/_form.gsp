<el:formGroup>
    <el:textField name="code" size="8" class=" " label="${message(code: 'job.code.label', default: 'code')}"
                  value="${job?.code}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean: job?.descriptionInfo]"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="jobCategory"
                     action="autocomplete" name="jobCategory.id"
                     label="${message(code: 'job.jobCategory.label', default: 'jobCategory')}"
                     values="${[[job?.jobCategory?.id, job?.jobCategory?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="educationDegreeAutoComplete"
                     name="educationDegrees"
                     label="${message(code: 'job.educationDegrees.label', default: 'educationDegrees')}"
                     values="${job?.transientData?.educationDegreeMapList}"
                     multiple="true"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="operationalTask"
                     action="autocomplete"
                     name="operationalTask"
                     label="${message(code: 'job.operationalTask.label', default: 'operationalTask')}"
                     values="${job?.joinedJobOperationalTasks?.collect {
                         [it.operationalTask.id, it.operationalTask.descriptionInfo.localName]
                     }}"
                     multiple="true"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="militaryRank"
                     action="autocomplete"
                     name="militaryRank"
                     label="${message(code: 'job.militaryRank.label', default: 'militaryRank')}"
                     values="${job?.joinedJobMilitaryRanks?.collect {
                         [it.militaryRank.id, it.militaryRank.descriptionInfo.localName]
                     }}"
                     multiple="true"/>
</el:formGroup>



<el:formGroup class="inspectionCategoriesDiv">
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="inspectionCategory"
                     action="autocomplete"
                     name="inspectionCategory"
                     paramsGenerateFunction="InspectionCategoriesParams"
                     id="inspectionCategories"
                     label="${message(code: 'jobRequisition.inspectionCategories.label', default: 'inspection Category')}"
                     values="${job?.joinedJobInspectionCategories?.collect {
                         [it.inspectionCategory.id, it.inspectionCategory.descriptionInfo.localName]
                     }}"
                     multiple="true"/>
</el:formGroup>



<div class="form-group ">
    <div class="col-sm-8 pcp-form-control ">
        <label class="col-sm-4 control-label no-padding-right text-left">
            ${message(code: 'job.age.label', default: 'Age')}
        </label>

        <div class="col-sm-8">
            <div id="age" class="input-group">
                <input id="fromAge"
                       value="${job?.fromAge}"
                       class="form-control isNumber input-integer null"
                       type="text"
                       name="fromAge">
                <span class="input-group-addon">
                    <i class="ace-icon icon-sort-numeric"></i>
                </span>
                <input id="toAge"
                       value="${job?.toAge}"
                       class="form-control isNumber input-integer"
                       type="text"
                       name="toAge">
            </div>
        </div>
    </div>

</div>


<div class="form-group ">
    <div class="col-sm-8 pcp-form-control ">
        <label class="col-sm-4 control-label no-padding-right text-left">
            ${message(code: 'job.height.label', default: 'Height')}
        </label>
        <div class="col-sm-8">
            <div id="height" class="input-group">
                <input id="fromHeight"
                       value="${job?.fromHeight}"
                       class="form-control isDecimal input-decimal null"
                       type="text"
                       name="fromHeight">
                <span class="input-group-addon">
                    <i class="ace-icon icon-sort-numeric-outline"></i>
                </span>
                <input id="toHeight"
                       value="${job?.toHeight}"
                       class="form-control isDecimal input-decimal"
                       type="text"
                       name="toHeight">
            </div>
        </div>
    </div>
</div>


<div class="form-group ">
    <div class="col-sm-8 pcp-form-control ">
        <label class="col-sm-4 control-label no-padding-right text-left">
            ${message(code: 'job.weight.label', default: 'Weight')}
        </label>
        <div class="col-sm-8">
            <div id="weight" class="input-group">
                <input id="fromWeight"
                       value="${job?.fromWeight}"
                       class="form-control isDecimal input-decimal null"
                       type="text"
                       name="fromWeight">
                <span class="input-group-addon">
                    <i class="ace-icon icon-sort-numeric-outline"></i>
                </span>
                <input id="toWeight"
                       value="${job?.toWeight}"
                       class="form-control isDecimal input-decimal"
                       type="text"
                       name="toWeight">
            </div>
        </div>
    </div>
</div>




<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'job.universalCode.label', default: 'universalCode')}"
                  value="${job?.universalCode}"/>
</el:formGroup>

<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'job.note.label', default: 'note')}"
                 value="${job?.note}"/>
</el:formGroup>
<script>
    $("#inspectionCategories").on("select2:close", function (e) {
        removeCloseBtn();
    });
</script>