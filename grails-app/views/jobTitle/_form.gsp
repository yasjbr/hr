<g:render template="/DescriptionInfo/wrapper" model="[bean: jobTitle?.descriptionInfo]"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="jobCategory"
                     action="autocomplete" name="jobCategory.id"
                     label="${message(code: 'jobTitle.jobCategory.label', default: 'jobCategory')}"
                     values="${[[jobTitle?.jobCategory?.id, jobTitle?.jobCategory?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="pcore"
                     action="educationDegreeAutoComplete"
                     name="educationDegrees"
                     label="${message(code: 'jobTitle.educationDegrees.label', default: 'educationDegrees')}"
                     values="${jobTitle?.transientData?.educationDegreeMapList}"
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
                     label="${message(code: 'jobTitle.operationalTask.label', default: 'operationalTask')}"
                     values="${jobTitle?.joinedJobTitleOperationalTasks?.collect {
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
                     label="${message(code: 'jobTitle.militaryRank.label', default: 'militaryRank')}"
                     values="${jobTitle?.joinedJobTitleMilitaryRanks?.collect {
                         [it.militaryRank.id, it.militaryRank.descriptionInfo.localName]
                     }}"
                     multiple="true"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="JobRequirement"
                     action="autocomplete"
                     name="JobRequirement"
                     label="${message(code: 'jobTitle.JobRequirement.label', default: 'JobRequirement')}"
                     values="${jobTitle?.joinedJobTitleJobRequirements?.collect {
                         [it.jobRequirement.id, it.jobRequirement.descriptionInfo.localName]
                     }}"
                     multiple="true"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'jobTitle.universalCode.label', default: 'universalCode')}"
                  value="${jobTitle?.universalCode}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class="" label="${message(code: 'jobTitle.note.label', default: 'note')}"
                 value="${jobTitle?.note}"/>
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="allowToRepeetInUnit" size="8" class="" label="${message(code: 'jobTitle.allowToRepeetInUnit.label', default: 'allowToRepeetInUnit')}"
                 isChecked="${jobTitle?.allowToRepeetInUnit}" value="${jobTitle?.allowToRepeetInUnit}"/>
</el:formGroup>

