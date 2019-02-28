// set the dimensions and margins of the graph
var margin = {top: 40, right: 10, bottom: 40, left: 50},
width = window.outerWidth - margin.left - margin.right,
height = window.outerHeight - margin.top - margin.bottom;

var data = [];

function loadData(chunk) {
    data = data.concat(chunk);
}

function initGraph() {
  // append the svg object to the body of the page
    var svg = d3.select("body").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
      .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  showGraph(svg, height, width);
}

//Read the data
function showGraph(svg, height, width) {
  // Labels of row and columns
  var myGroups = d3.map(data, function(d) {return d.x;}).keys()
  var myVars = d3.map(data, function(d) {return d.y;}).keys()
  var zMin = Math.min.apply(null, d3.map(data, function(d) {return d.z;}).keys())
  var zMax = Math.max.apply(null, d3.map(data, function(d) {return d.z;}).keys())

  // Build X scales and axis
  var x = d3.scaleBand()
    .range([ 0, width ])
    .domain(myGroups)

  svg.append("g")
    .style("font-size", 15)
    .attr("transform", "translate(0," + height + ")")
    .call(d3.axisBottom(x).tickFormat(d3.formatPrefix(".1", 1e1)).tickValues(x.domain().filter(function(d,i){ return !(i%10)})))
    .select(".domain").remove()

  // Build Y scales and axis - bit messy
  var y = d3.scaleBand()
    .range([ height, 0 ])
    .domain(myVars)

  svg.append("g")
      .style("font-size", 15)
      .call(d3.axisLeft(y).tickFormat(d3.formatPrefix(".1", 1e1)).tickValues(y.domain().filter(function(d,i){ return !(i%10)})))
      .select(".domain").remove()

  // Build color scale
  var myColor = d3.scaleSequential()
    .interpolator(d3.interpolateInferno)
    .domain([zMin,zMax])

  // Add the rectangles
  svg.selectAll()
    .data(data, function(d) {return d.x+':'+d.y;})
    .enter()
    .append("rect")
      .attr("x", function(d) { return x(d.x) })
      .attr("y", function(d) { return y(d.y) })
      .attr("width", x.bandwidth())
      .attr("height", y.bandwidth())
      .style("fill", function(d) { return myColor(d.z)} )
      .style("stroke", "none")
      .style("opacity", 0.8)
}
