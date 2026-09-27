package cn.duckflew.education.file;

public record FileInfo(
        Long id,
        String originalName,
        String contentType,
        long size,
        String url
) {
    public static FileInfo from(FileObject file, String publicBaseUrl) {
        return new FileInfo(file.getId(), file.getOriginalName(), file.getContentType(),
                file.getSizeBytes(), publicBaseUrl + "/" + file.getId());
    }
}
