import CssBaseline from '@mui/material/CssBaseline';
import Container from '@mui/material/Container';

export default function PreviewContainer({children}) {
    return (
        <>
            <CssBaseline/>
            <Container maxWidth="md" sx={{marginBottom: '10px'}}>
                {children}
            </Container>
        </>
    );
}