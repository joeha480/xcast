<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
	<xsl:output method="xml" indent="no" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	 doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
	
	<!-- Ersätt hd[parent::list] med li -->
	<xsl:template match="hd[parent::list]">
		<li>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</li>
	</xsl:template>
	
	<!-- Ersätt dtbook med html -->
	<xsl:template match="dtbook">
		<html>
			<xsl:copy-of select="@*"/>
			<head>
				<title></title>
			</head>
			<body>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	
	<!-- Platta -->
	<xsl:template match="head|book|frontmatter|bodymatter|rearmatter">
		<xsl:apply-templates/>
	</xsl:template>
	
	<!-- Ersätt sidebar med div -->
	<xsl:template match="sidebar">
		<div class="sidebar">
			<xsl:copy-of select="@*[not(name()='render')]"/>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
	
	<!-- Ersätt prodnote med span -->
	<xsl:template match="prodnote">
		<span class="prodnote">
			<xsl:copy-of select="@*[not(name()='render' or name()='imgref')]"/>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	
	<!-- Ersätt pagenum med span -->
	<xsl:template match="pagenum">
		<span class="page-normal">
			<xsl:copy-of select="@*[not(name()='page')]"/>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	
	<!-- Ta bort pagenum[parent::list] (behandlas under li) -->
	<xsl:template match="pagenum[parent::list]"/>
	
	<xsl:template match="li[(following-sibling::*)[1][self::pagenum]]">
		<li>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
			<xsl:for-each select="
				following-sibling::pagenum[generate-id(preceding-sibling::li[1])=generate-id(current())]">
				<span class="page-normal">
					<xsl:copy-of select="@*[not(name()='page')]"/>
					<xsl:apply-templates/>
				</span>
			</xsl:for-each>
		</li>
	</xsl:template>

	<!-- Ersätt med div -->
	<xsl:template match="level|level1|level2|level3|level4|level5|level6">
		<div>
			<xsl:copy-of select="@*"/>
			<xsl:attribute name="class"><xsl:value-of select="name()"/></xsl:attribute>
			<xsl:apply-templates/>
		</div>
	</xsl:template>
		
	<!-- Ersätt med span -->
	<xsl:template match="imggroup|hd|lic">
		<span>
			<xsl:copy-of select="@*"/>
			<xsl:attribute name="class"><xsl:value-of select="name()"/></xsl:attribute>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
		
	<!-- Ersätt caption med span -->
	<xsl:template match="caption">
		<span class="caption">
			<xsl:copy-of select="@*[not(name()='imgref')]"/>
			<xsl:apply-templates/>
		</span>
	</xsl:template>
	
	<!-- Ersätt caption[ancestor::table] med caption -->
	<xsl:template match="caption[ancestor::table]">
		<caption>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</caption>
	</xsl:template>
	
	<!-- Ersätt list med ul -->
	<xsl:template match="list">
		<ul class="pl">
			<xsl:copy-of select="@*[not(name()='type')]"/>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>

	<xsl:template match="code">
		<code>
			<xsl:copy-of select="@*[not(name()='xml:space')]"/>
			<xsl:apply-templates/>
		</code>
	</xsl:template>

	<!-- Ersätt list[ancestor::prodnote] med ul -->
	<xsl:template match="list[ancestor::prodnote]">
		<iframe>
			<ul class="pl">
				<xsl:copy-of select="@*[not(name()='type')]"/>
				<xsl:apply-templates/>
			</ul>
		</iframe>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="comment()">
		<xsl:copy-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
