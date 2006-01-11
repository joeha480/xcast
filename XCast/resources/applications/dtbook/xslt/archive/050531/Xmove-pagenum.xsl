<?xml version="1.0" encoding="UTF-8"?>
<!--
		*** Move pagenum ***
		Flyttar sidnummer som finns innuti hx före hx-taggen och sidnummer som ligger innuti ord till efter ordet.
		Joel Håkansson, TPB
		Version 2005-05-16
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no"/>



<!-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * -->

<!-- Flyttar pagenum innuti hx -->
<xsl:template match="h1|h2|h3|h4|h5|h6|hd">
  <!-- kopiera alla pagenum-taggar under hx -->
  <xsl:for-each select="descendant::pagenum">
    <xsl:copy-of select="."/>
  </xsl:for-each>
  <xsl:call-template name="copy"/>
</xsl:template>

<!-- behandla inte pagenum innuti hx -->
<xsl:template match="pagenum[ancestor::h1|ancestor::h2|ancestor::h3|ancestor::h4|ancestor::h5|ancestor::h6|ancestor::hd]"/>

<!-- / Flyttar pagenum innuti hx -->



<!-- * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * -->

<!-- Flyttar pagenum innuti ord -->
<!-- Dessa behandlas under pagenum, ignoreras här -->
<xsl:template match="text()[preceding-sibling::node()[1][self::pagenum] and
							preceding-sibling::node()[2][self::text()]]"/>

<!-- Pagenum med textnoder på båda sidor. -->
<xsl:template match="pagenum[preceding-sibling::node()[1][self::text()] and 
							 following-sibling::node()[1][self::text()]]">

  <xsl:variable name="A1" select="following-sibling::node()[1]"/>
  <xsl:variable name="A2" select="preceding-sibling::node()[1]"/>
  <xsl:choose>
    <!-- 
          ends-with: substring($A, string-length($A) - string-length($B) + 1) = $B
                     Se XSLT programmers reference, second edition, Michael Kay, sidan 541
      -->
    <!-- 
         Om föregående textnod slutar med mellanslag eller om nästkommande textnod börjar med mellanslag
         så ska denna tagg inte flyttas.
      -->
    <xsl:when test="starts-with($A1, ' ') or substring($A2, string-length($A2))=' '">
      <xsl:call-template name="copy"/>
      <xsl:value-of select="$A1"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:choose>
        <xsl:when test="contains($A1,' ')">
          <xsl:value-of select="substring-before($A1,' ')"/>
          <xsl:call-template name="copy"/>
          <xsl:value-of select="concat(' ',substring-after($A1,' '))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$A1"/>
          <xsl:call-template name="copy"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
<!-- /Flyttar pagenum innuti ord -->



<xsl:template match="*">
  <xsl:call-template name="copy"/>
</xsl:template>

<xsl:template name="copy">
  <xsl:element name="{name()}">
  	<xsl:for-each select="@*">
  	  <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
  	</xsl:for-each>
  	<xsl:apply-templates/>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>