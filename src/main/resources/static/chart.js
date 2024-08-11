// 定义柱状图绘制的函数
function renderBarChart(data) {
    const margin = {top: 20, right: 30, bottom: 40, left: 40};
    const width = 800 - margin.left - margin.right;
    const height = 500 - margin.top - margin.bottom;

    const svg = d3.select("#bar-chart").append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", `translate(${margin.left},${margin.top})`);

    const x = d3.scaleBand()
        .domain(data.map(d => d.name))
        .range([0, width])
        .padding(0.1);

    const y = d3.scaleLinear()
        .domain([0, d3.max(data, d => d.value)])
        .nice()
        .range([height, 0]);

    // 纵轴仅显示整数
    const yAxis = d3.axisLeft(y).ticks(d3.max(data, d => d.value)).tickFormat(d3.format("d"));

    svg.append("g")
        .attr("class", "x-axis")
        .attr("transform", `translate(0,${height})`)
        .call(d3.axisBottom(x))
        // 新增超链接
        .selectAll("text")
        .attr("class", "x-axis-text")
        .attr("dx", "0.5em")
        .attr("dy", "0.5em")
        .attr("text-anchor", "middle")
        .style("cursor", function(d) {
            //仅当有链接时，鼠标指针变为手形
            return links[d] ? "pointer" : "default";
        })
        .on("click", function(event, d) {
            // 获取对应的文件夹链接
            const folderLink = links[d];
            if (folderLink) {
                d3.select(this)
                .append("title")
                .text("点击查看详细信息"); // 可以修改为实际
                window.open(folderLink);
            }})
        
        .on("mouseover", function(event, d) {
            // 悬停时显示提示信息
            const folderLink = links[d];
            if (folderLink) {
                // 如果有链接，显示点击查看详细信息
                d3.select(this)
                    .append("title")
                    .text("点击查看详细信息");
            } else {
                // 如果没有链接，根据文件夹名称显示不同提示信息
                let tooltipText = "暂无信息";
                if (d === "clean") {
                    tooltipText = "正常图像";
                } else if (d === "ours") {
                    tooltipText = "我们的方法";
                }
    
                d3.select(this)
                    .append("title")
                    .text(tooltipText);
            }
        })
        .on("mouseout", function() {
            // 移除提示信息
            d3.select(this).select("title").remove();
        });

    svg.append("g")
        .attr("class", "y-axis")
        .call(yAxis);

    svg.selectAll(".bar")
        .data(data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("x", d => x(d.name))
        .attr("y", d => y(d.value))
        .attr("width", x.bandwidth())
        .attr("height", d => height - y(d.value))
        .attr("fill", "rgb(128, 128, 128)");

    svg.selectAll(".label")
        .data(data)
        .enter().append("text")
        .attr("class", "label")
        .attr("x", d => x(d.name) + x.bandwidth() / 2)
        .attr("y", d => y(d.value) - 5)
        .attr("text-anchor", "middle")
        .text(d => d.value)
        .style("font-size", "16px"); // 提高字体大小
}
// 预定义的文件夹链接
const links = {
    "CLBA": "https://arxiv.org/pdf/1912.02771",
    "DA": "https://ieeexplore.ieee.org/abstract/document/10208211",
    "HTBA": "https://ojs.aaai.org/index.php/AAAI/article/view/6871",
    "lsbPanda": "http://www.cjig.cn//html/2023/3/20230317.htm",
    "refool": "https://arxiv.org/pdf/2007.02343",
    "Sig": "https://ieeexplore.ieee.org/abstract/document/8802997",
    "Inv": "https://ieeexplore.ieee.org/document/9488902",
    "clean": "",
    "ours": "",
    "SAA": "https://proceedings.neurips.cc/paper_files/paper/2022/file/79eec295a3cd5785e18c61383e7c996b-Paper-Conference.pdf",
    // 添加更多映射
};