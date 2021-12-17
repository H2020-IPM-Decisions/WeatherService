# Quality control

## Thresholds

Thresholds for different QC tests are defined in the resource file 
`thresholddata.json` found in `src/main/resources`.

### Interval test

#### Min and max limits - `lower_limit` and `upper_limit`

Interval test uses thresholds `lower_limit` and `upper_limit` to define the 
minimum and maximum values allowed for a weather parameter.

These thresholds are exclusive, meaning any value equal to (or smaller for 
`lower_limit` and greater for `upper_limit`) is consider invalid.

There are no default values for either of these thresholds - a weather 
parameter missing the threshold values should be considered always valid for 
interval QC testing.

### Step test

#### Step threshold size - `step_test_threshold`

Step test uses the threshold value `step_test_threshold` for defining 
threshold between valid and invalid step sizes. 

This threshold is exclusive, meaning that any value the size of the threshold 
(or over) is considered invalid. 

There is no default threshold for `step_test_threshold`. Any weather parameter
missing the `step_test_threshold` is considered to be valid for all steps.

#### Step threshold type - `step_test_threshold_type`

In addition, step test uses the value `step_test_threshold_type` to define 
whether step size should be compared using either `absolute` or `relative` 
size of the step.

### Freeze test

#### Freeze length threshold - `freeze_test_threshold`

Freeze QC test uses `freeze_test_threshold` for defining a threshold between
valid and invalid freeze lengths.

This threshold is incluside, and defines the largest valid threshold for 
consecutive hours of value freeze for a weather parameter in freeze QC test. 
Any values over the threshold are considered invalid.

There is a default threshold of 5 consecutive hours for `freeze_test_threshold`.
If no `freeze_test_threshold` is defined for a weather parameter, the default
value of 5 is considered as its inclusive threshold.
